package ra.views;

import ra.config.InputMethods;
import ra.config.Validate;
import ra.model.User;
import ra.service.*;

import java.time.LocalDate;
import java.util.List;

import static ra.config.ConsoleColor.*;
import static ra.config.InputMethods.*;
import static ra.config.Validate.isValidFullName;
import static ra.config.Validate.isValidPassword;
import static ra.constant.Contant.ADMIN_CODE;
import static ra.constant.Contant.Importance.BLOCK;
import static ra.constant.Contant.Importance.OPEN;
import static ra.constant.Contant.Role.ADMIN;
import static ra.constant.Contant.Status.ACTIVE;


public class UserViews {

    private UserService userService;

    private CartService cartService;
    private ProductService productService;
    private OrderService orderService;
    private CategoryService categoryService;

    public UserViews() {

        this.userService = new UserService();

        this.productService = new ProductService();
        this.orderService = new OrderService();
        this.categoryService = new CategoryService();
    }

    public static void main(String[] args) {
        UserViews userViews = new UserViews();
        userViews.loginOrRegister();
    }


    public  void loginOrRegister() {
        print(GREEN);
        System.out.println("╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║                  Wellcome to Rikkei--Shop                 ║");
        System.out.println("╟-----------------------------------------------------------╢");
        System.out.println("║   1    │                  Đăng nhập                       ║");
        System.out.println("║   2    │                  Đăng ký                         ║");
        System.out.println("║   0    │                  Thoát                           ║");
        System.out.println("╚════════╧══════════════════════════════════════════════════╝");
        System.out.println("Nhập vào lựa chọn của bạn :1/2/0 ");

        int choice = InputMethods.getInteger();
        switch (choice) {
            case 1:
               User user = login();
                break;
            case 2:
                User user1 = registerUser();
                userService.save(user1);
                printlnSuccess("Đăng ký thành công !");
                loginOrRegister();
                break;
            case 0:
                break;
    }
    }
    private User registerUser() {
        List<User> users = userService.findAll();
        User user = new User();
        user.setId(userService.autoInc());
        printlnMess("Vui lòng đăng ký tài khoản !!");
        System.out.println(" Nhập vào thông tin đăng nhập");
        // Nhập email
        while (true) {
            System.out.println(" Nhập vào email đăng ký: ");
            String email = InputMethods.scanner().nextLine();

            if (Validate.isValidEmail(email)) {
                boolean isEmailAvailable = true;

                if (users != null) {
                    for (User existingUser : users) {
                        if (existingUser.getEmail().trim().equals(email)) {
                            printlnError("Email đã được sử dụng, mời nhập email mới.");
                            isEmailAvailable = false;
                            break;
                        }
                    }
                } else {
                    isEmailAvailable = false;
                }

                if (isEmailAvailable) {
                    user.setEmail(email);
                    break; // Kết thúc vòng lặp khi email hợp lệ và không trùng lặp
                }
            }
        }

        // Nhập số điện thoại
        while (true) {
            System.out.println("Nhập vào số điện thoại: ");
            String phone = InputMethods.scanner().nextLine();

            if (Validate.isValidPhone(phone)) {
                boolean isPhoneAvailable = true;
                if (users != null) {
                    for (User existingUser : userService.findAll()) {
                        if (existingUser.getPhone().trim().equals(phone)) {
                            printlnError("Số điện thoại đã được sử dụng, mời nhập số điện thoại mới.");
                            isPhoneAvailable = false;
                            break;
                        }
                    }
                } else {
                    isPhoneAvailable = false;
                }

                if (isPhoneAvailable) {
                    user.setPhone(phone);
                    break; // Kết thúc vòng lặp khi số điện thoại hợp lệ và không trùng lặp
                }
            }
        }

        // Nhập địa chỉ
        while (true) {
            System.out.println("Hãy nhập vào địa chỉ: ");
            String address = InputMethods.scanner().nextLine();

            if (Validate.isValidAddress(address)) {
                user.setAddress(address);
                break;
            }
        }


        // Nhập họ và tên đầy đủ
        while (true) {
            System.out.println("Nhâp vào họ và tên đầy đủ: ");
            String fullName = InputMethods.scanner().nextLine();

            if (Validate.isValidFullName(fullName)) {
                user.setFullName(fullName);
                break;
            }
        }

        // Nhập tên đăng nhập
        while (true) {
            System.out.println("Nhập tên đăng nhập: ");
            String username = InputMethods.scanner().nextLine();

            if (Validate.isValidFullName(username)) {
                boolean isUsernameAvailable = true;

                if (users != null) {
                    for (User existingUser : users) {
                        if (existingUser.getUsername().trim().equals(username)) {
                            printlnError("Tên đăng nhập đã được sử dụng");
                            isUsernameAvailable = false;
                            break;
                        }
                    }
                } else {
                    isUsernameAvailable = false;
                }

                if (isUsernameAvailable) {
                    user.setUsername(username);
                    break; // Kết thúc vòng lặp khi tên đăng nhập hợp lệ và không trùng lặp
                }
            }
        }

        // Nhập mật khẩu
        while (true) {
            System.out.println("Nhập vào mật khẩu: ");
            String password = InputMethods.scanner().nextLine();

            if (Validate.isValidPassword(password)) {
                user.setPassword(password);
                break;
            }
        }


        System.out.println("Hãy chọn role của bạn: ");
        System.out.println("1: ADMIN");
        System.out.println("2: USER");
        int role = getInteger();
        if (role == ADMIN) {
            // Nếu là ADMIN, yêu cầu nhập mã xác nhận ADMIN
            printlnMess("Nhập vào mã xác nhận ADMIN: ");
            String adminCode = getString();

            if (!adminCode.equals(ADMIN_CODE)) {
                printlnError("Mã xác thực không đúng, vui lòng nhập lại.");
                return registerUser(); // Gọi lại phương thức để người dùng nhập lại
            }
        }

        user.setRole(role);

        user.setCreateAt(LocalDate.now());

        // Đăng ký hoàn thành, trả về đối tượng User đã tạo
        return user;
    }

    private User login() {
        String pass;
        String userName;
        printlnMess("Thực hiện đăng nhập:");
        while (true) {
            System.out.println("Tên đăng nhập: ");
            userName = getString();
            if (isValidFullName(userName)) {
                break;
            }
        }
        while (true) {
            System.out.println("Password: ");
            pass = scanner().nextLine();
            if (isValidPassword(pass)) {
                break;
            }
        }
        User user;
        user = userService.login(userName, pass);

        if (user != null) {
            if (user.isImportance()) {
                userService.setStatusLogin(userName, ACTIVE);
                if (user.getRole() == ADMIN) {

                    displayAdminMenu();

                } else {
                    displayUserMenu();
                }

            } else {
                printlnError("Tài khoản của bạn đã bị khóa !!");
                loginOrRegister();
            }


        } else {
            printlnError("Đăng nhập thấy bại,Mật khẩu hoặc UserName ko trùng hợp!!! ");
            loginOrRegister();

        }
        return user;
    }
    private void displayUserMenu() {
        User user1 = new UserService().userActive();

        int choice;
        do {
            print(GREEN);
            System.out.println("╔═══════════════════════════════════════════════════════════╗");
            System.out.println(String.format("║  Rikkei-shop                 %-20s  ", user1.getUsername()));
            System.out.println("╟-----------------------------------------------------------╢");
            System.out.println("║   1    │                 Trang chủ                        ║");
            System.out.println("║   2    │                 Giỏ hàng                         ║");
            System.out.println("║   0    │                  Thoát                           ║");
            System.out.println("╚════════╧══════════════════════════════════════════════════╝");
            System.out.println("Nhập vào lựa chọn của bạn: 1/2/0");
            printFinish();

            choice = getInteger();
            switch (choice) {
                case 1:
                   new ProductViews().displayUserMenuProduct();
                    break;

                case 2:
                    new CartView().displayMenuCart();
                    break;
                case 3:
                    logout();
                    break;
            }
        } while (choice != 5);
    }

    public void logout() {
        System.out.println("Bạn chắc chắn muốn thoát chứ ??");
        System.out.println("1. Có          ");
        System.out.println("2.Không");
        int choice = InputMethods.getInteger();
        if(choice == 1) {
            loginOrRegister();
        }
    }

    public void displayAdminMenu() {
        User use1 = new UserService().userActive();
        int choice;
        do {
            print(GREEN);
            System.out.println("╔════════════════════════════════════════════════════════╗");
            System.out.println(String.format("║  Rikkei-shop                 %-20s  ", userService.userActive().getUsername()));
            System.out.println("╢--------------------------------------------------------");
            System.out.println("║   1    │    Quản lý người dùng                         ║");
            System.out.println("║   2    │    Quản lý danh mục                           ║");
            System.out.println("║   3    │    Quản lý sản phẩm                           ║");
            System.out.println("║   4    │    Quản lý Đơn hàng                           ║");
            System.out.println("║   5    │    Quay lại menu trước                        ║");
            System.out.println("║   6    │    Đăng xuất                                  ║");
            System.out.println("╚════════╧═══════════════════════════════════════════════╝");
            System.out.println("Nhập vào lựa chọn của bạn : ");
            printFinish();

            choice = getInteger();
            switch (choice) {
                case 1:
                    userManagement();
                    break;

                case 2:
                   new CategoryView().displayAdminCategory();
                    break;
                case 3:
                   new ProductViews().displayMenuAdminMenuProduct();
                    break;
                case 4:
                   new OrderView().menuAdminOrder();
                    break;
                case 5:
                    return;
                case 6:
                    logout();
                    break;
                default:
                    break;
            }
        } while (choice != 7);
    }
    private void userManagement() {
        int choice;

        do {
            print(CYAN);
            System.out.println("╔══════════════════════════════════════════════════════════╗");
            System.out.println(String.format("║  Rikkei-shop                 %-20s  ", userService.userActive().getUsername()));
            System.out.println("╟----------------------------------------------------------╢");
            System.out.println("║   1    │    Danh sách người dùng                         ║");
            System.out.println("║   2    │    Tìm kiếm ngừoi dùng theo tên                 ║");
            System.out.println("║   3    │    Quản lý trạng thái ngừoi dùng (Block)        ║");
            System.out.println("║   4    │    Quay lại menu trước                          ║");
            System.out.println("║   5    │    Đăng xuất                                    ║");
            System.out.println("╚════════╧═════════════════════════════════════════════════╝");
            System.out.println("Nhập vào lựa chọn của bạn: ");
            choice = getInteger();
            printFinish();

            switch (choice) {
                case 1:
                    UserList();
                    break;
                case 2:
                    displayUserByUserName();

                    break;
                case 3:
                    changeUserImportance();
                    break;
                case 4:
                    return;
                case 5:
                    logout();
                    break;
                default:
                    break;
            }
        } while (true);
    }

    private void changeUserImportance() {
        System.out.println("Hãy nhập username bạn muốn thay đổi trạng thái:");
        String username = getString();
        User user = userService.getUserByUsename(username);
        if(user == null) {
            System.err.println("Không tìm thấy username bạn muốn đổi trạng thái !!\"");

        } else  {
            if(user.getRole() == ADMIN) {
                printlnError("Không thể khóa user ADMIN !!");
            } else {
                userService.updateImportance((user.isImportance() == OPEN ? BLOCK : OPEN), username);
                printlnSuccess("Thay đổi trạng thái thành công!");
            }
        }
    }

    private void displayUserByUserName() {
        System.out.println("Nhập vào tên User cần tìm kiếm");
        String username = getString();
        List<User> fitterUsers = userService.getFitterUsers(username);
        if(fitterUsers.size() != 0) {
            System.out.println("Danh sách User:");
            System.out.println("|-----------------------------------------------------------------------------------------------------------------|");
            System.out.println(String.format("|%-8s|%-20s|%-20s|%-10s|%-15s|%-15s|%-15s|",
                    "Id User", "Tên người dùng", "Email", "Role", "Thời gian tạo", "Thời gian cập nhật", "Importance"));
            System.out.println("|-----------------------------------------------------------------------------------------------------------------|");
                for (User user: fitterUsers) {
                user.display();
            }
        } else  {
            printlnError("Không thể khóa user ADMIN !!");

        }
    }

    private void UserList() {
        List<User> sortUsers = userService.getSortUsersList();
       if(sortUsers.size() != 0) {
           System.out.println("Danh sách User sắp xếp theo tên");
           System.out.println("|-----------------------------------------------------------------------------------------------------------------|");
           System.out.println(String.format("|%-8s|%-20s|%-20s|%-10s|%-15s|%-15s|%-15s|",
                   "Id User", "Tên người dùng", "Email", "Role", "Thời gian tạo", "Thời gian cập nhật", "Importance"));
           System.out.println("|-----------------------------------------------------------------------------------------------------------------|");
           for (User user: sortUsers) {
               user.display();

           }
       } else  {
           printlnError("Không thể khóa user ADMIN !!");

       }

    }
}


