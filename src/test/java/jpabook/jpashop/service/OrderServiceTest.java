package jpabook.jpashop.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.exception.NotEnoughStockException;
import jpabook.jpashop.repository.OrderRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
public class OrderServiceTest {

    @PersistenceContext
    EntityManager em;

    @Autowired
    OrderService orderService;
    @Autowired
    OrderRepository orderRepository; // 왜 롬복 안 쓰지

    @Test
    public void 상품주문() throws Exception{

        //Given
        Member member = createMember();
        Item item = createBook("시골 JPA",10000,10);
        int orderCount = 2;
        //When
        Long orderId = orderService.order(member.getId(),item.getId(),orderCount);
        //Then
        Order getOrder = orderRepository.findOne(orderId);

        Assertions.assertEquals(OrderStatus.ORDER,getOrder.getStatus(),"상품 주문시 상태는 ORDER");
        Assertions.assertEquals(1,getOrder.getOrderItems().size(),"리스트 사이즈가 주문한 상품 종류 수와 일치");
        Assertions.assertEquals(10000*2,getOrder.getTotalPrice(),"총 금액이 상품 금액과 상품 수의 곱");
        Assertions.assertEquals(8,item.getStockQuantity(),"주문하면 재고 수가 감소");
    }

    @Test
    public void 상품주문_재고수량초과(){
        //given
        Member member = createMember();
        Item item = createBook("시골 JPA",10000,10);

        int orderCount = 11;
        //when, then
        Assertions.assertThrows(NotEnoughStockException.class,()-> orderService.order(member.getId(), item.getId(), orderCount));
    }

    @Test
    public void 주문취소(){
        //given
        Member member = createMember();
        Item item = createBook("시골 JPA",10000,10);
        int orderCount = 2;

        Long orderId = orderService.order(member.getId(), item.getId(), orderCount);

        //when
        orderService.cancelOrder(orderId);

        //then
        Order getOrder = orderRepository.findOne(orderId);
        Assertions.assertEquals(OrderStatus.CANCEL,getOrder.getStatus(), "주문 취소 시 상태는 CANCEL");
        Assertions.assertEquals(10,item.getStockQuantity(),"주문 취소된 상품은 재고 증가");
    }

    private Member createMember(){
        Member member = new Member();
        member.setName("회원1");
        member.setAddress(new Address("강릉","바다","111-111"));
        em.persist(member);
        return member;
    }

    private Book createBook(String name, int price, int stockQuantity){
        Book book = new Book();
        book.setName(name);
        book.setStockQuantity(stockQuantity);
        book.setPrice(price);
        em.persist(book);
        return book;
    }
}
