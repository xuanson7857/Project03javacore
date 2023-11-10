package ra.views;

import ra.config.Validate;
import ra.model.Cart;
import ra.model.Order;
import ra.model.Product;
import ra.model.User;
import ra.service.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static ra.config.ConsoleColor.*;
import static ra.config.InputMethods.*;

public class CartView {

    private UserViews userViews;
    private UserService userService;
    private CartService cartService;
    private ProductService productService;
    private OrderService orderService;
    private CategoryService categoryService;

    public CartView() {
        this.userViews = new UserViews();
        this.userService = new UserService();
        this.cartService = new CartService();
        this.productService = new ProductService();
        this.orderService = new OrderService();
        this.categoryService = new CategoryService();
    }


    public void displayMenuCart() {
        print(GREEN);
        int selectCart;
        while (true) {
            System.out.println("╔══════════════════════════════════════╗");
            System.out.println(String.format("║  Rikkei-shop                 %-20s  ", userService.userActive().getUsername()));
            System.out.println("╟────────┬─────────────────────────────╢");
            System.out.println("║   1    │  Xem danh sách giỏ hàng     ║");
            System.out.println("║   2    │  Thay đổi số lượng          ║");
            System.out.println("║   3    │  Xóa sản phẩm trong gio hang║");
            System.out.println("║   4    │  Thanh toán                 ║");
            System.out.println("║   5    │  Lich sử mua hàng           ║");
            System.out.println("║   6    │  Quay lại menu              ║");
            System.out.println("║   7    │  Đăng xuất                  ║");
            System.out.println("╚════════╧═════════════════════════════╝");
            System.out.println("Nhap lua chon cua ban : ");
            selectCart = getInteger();
            switch (selectCart) {
                case 1:

                    displayAllCart();
                    break;
                case 2:
                    updateQuantity();
                    break;
                case 3:
                    deleteProduct();
//                    deleteProductInCart();
                    break;
//                case 4:
//                    deleteAllProductInCart();
//                    break;
                case 4:
                    endPay();
                    break;
                case 5:
                    new OrderView().OrderMenuHistory();
                    break;
                case 6:
                    return;
                case 7:
                    new UserViews().logout();
                    break;
                default:
                    System.err.println("--->> Lua chon khong phu hop. Vui long chon lai  ");
                    break;
            }
        }
    }

    private void deleteProduct() {
        int choice;
        do{
            System.out.println("-----------------------------------------------");
            System.out.println("1: xoá 1 sản phẩm       2:xoá toàn bộ sản phẩm           3:Quay lại menu trước"     );
            System.out.println("xin moi lưa chọn");
            choice=getInteger();
            switch (choice){
                case 1:deleteProductInCart();
                break;
                case 2:deleteAllProductInCart();
                break;
                default:
                    break;
            }
        }while (choice !=3);
    }

    private void displayAllCart() {
        List<Cart> carts = cartService.findAll();
        if (carts.isEmpty()) {
            printlnError("Giỏ hàng rỗng !!.");
            return;
        }
        System.out.println("|---------------------------------------CART----------------------------------------|");
        System.out.println(String.format("|%-20s|%-20s|%-20s|%-20s|","ID","Tên sản phẩm","Số lượng","Giá"));
        System.out.println("|-----------------------------------------------------------------------------------|");

        for (Cart ca : carts
        ) {
            ca.display();
        }
    }

    private void updateQuantity() {
        System.out.println("Nhập vào ID: ");
        int idCart = getInteger();
        Cart cart = cartService.findById(idCart);
        if (cart == null) {
            printlnError("Sản phẩm không tồn tại trong giỏ hàng!!");
            return;
        }

        System.out.println("Nhập vào số lượng muốn cập nhật mới: ");
        int updateQuantity = getInteger();

        if (updateQuantity > cart.getProduct().getStock()) {
            printlnError("Số lượng sản phẩm vượt quá tồn kho.");
        } else {
            cart.setQuantity(updateQuantity);
            printlnSuccess("Cập nhật số lượng thành công .");
            cartService.save(cart);
        }
    }

    private void deleteProductInCart() {
        System.out.println("Nhập vào ID");
        int idCart = getInteger();
        int index = cartService.findIndex(idCart);

        if (index == -1) {
            System.err.println("Sản phẩm không tồn tại trong giỏ hàng.");
            return;
        }

        cartService.deleteCart(index);
    }

    private void deleteAllProductInCart() {
        cartService.deleteAll();
    }

    private void endPay() {

        User user = cartService.userLogin();
        if (user.getCart().isEmpty()) {
            printlnMess("Chưa có đơn hàng cần thanh toán !!.");
            return;
        }
        Order newOrder = new Order();
        newOrder.setId(orderService.autoInc());

        // Cập nhật tổng tiền
        double total = 0;
        for (Cart ca : user.getCart()) {
            total += ca.getProduct().getPrice() * ca.getQuantity();
        }
        newOrder.setTotal(total);
        newOrder.setIdUser((int) user.getId());
        System.out.println("Nhập tên người nhận hàng: ");
        newOrder.setReceiver(getString());
        System.out.println("Nhập số điện thoại người nhận: ");
        while (true) {
            String phone = scanner().nextLine();
            if (Validate.isValidPhone(phone)) {
                newOrder.setNumberPhone(phone);
                break;
            }
            ;
        }

        System.out.println("Nhập vào địa chỉ người nhận:");
        newOrder.setAddress(getString());
        newOrder.setBuyDate(LocalDate.now());

        // Tiến hành trừ đi số lượng trong kho hàng
        for (Cart ca : user.getCart()) {
            Product pt = productService.findById((int) ca.getProduct().getId());
            pt.setStock(pt.getStock() - ca.getQuantity());
            productService.updateQuantity(pt);
        }

        printlnSuccess("Đặt hàng thành công.Vui lòng chờ xác nhận !!.");
        orderService.save(newOrder);
        Order order = new Order();
        userService.save(user);
        order.setOrderDetail(user.getCart());
        user.setCarts(new ArrayList<>());
    }


}
