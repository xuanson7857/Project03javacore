package ra.views;

import ra.model.Category;
import ra.model.User;
import ra.service.CategoryService;
import ra.service.OrderService;
import ra.service.ProductService;
import ra.service.UserService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.util.Collections.*;
import static ra.config.ConsoleColor.*;
import static ra.config.InputMethods.*;
import static ra.constant.Contant.CategoryStatus.UNHIDE;

public class CategoryView {
    private UserViews userViews;
    private UserService userService;

    private ProductService productService;
    private OrderService orderService;
    private CategoryService categoryService;

    public CategoryView() {
        this.userViews = new UserViews();
        this.userService = new UserService();

        this.productService = new ProductService();
        this.orderService = new OrderService();
        this.categoryService = new CategoryService();
    }


    public void displayAdminCategory() {

        int choice;

        do {

            print(RED);
            System.out.println("╔════════════════════════════════════════════╗");
            System.out.println(String.format("║  Rikkei-shop                 %-20s  ", userService.userActive().getUsername()));
            System.out.println("╟────────┬───────────────────────────────────╢");
            System.out.println("║   1    │    Thêm mới danh mục              ║");
            System.out.println("║   2    │    Hiển thị danh mục              ║");
            System.out.println("║   3    │    Tìm danh mục theo tên          ║");
            System.out.println("║   4    │    Chỉnh sửa danh mục             ║");
            System.out.println("║   5    │    Quay lại menu trước            ║");
            System.out.println("║   6    │    Đăng xuất                      ║");
            System.out.println("╚════════╧═══════════════════════════════════╝");
            System.out.println("Nhập vào lựa chọn của bạn : ");
            printFinish();

            choice = getInteger();

            switch (choice) {
                case 1:
                    addCategory();
                    break;
                case 2:
                    displayAllCategorys();
                    break;
                case 3:
                    searchCategoryByName();
                    break;
                case 4:
                    editCategory();
                    break;

                case 5:
                    return;
                case 6:
                    if (userViews != null) {
                        userViews.logout();
                    }
                    break;
                default:
                    break;
            }

        } while (true);

    }


    private void editCategory() {
        System.out.println("Nhập vào id danh mục cần sửa: ");
        int id = getInteger();
        List<Category> allCategory = categoryService.findAll();

        int index = categoryService.findIndex(id);
        if (index != -1) {
            Category categoryToEdit = new Category();
            boolean isExit = true;
            categoryToEdit.setId(id);
            System.out.println("Nhập vào tên danh mục mới ");
            String newName = scanner().nextLine();
            if (!newName.trim().isEmpty()) {
                categoryToEdit.setCategoryName(newName);
            }

            System.out.println("Nhập vào mô tả danh mục mới :");
            String newDes = scanner().nextLine();
            if (!newDes.trim().isEmpty()) {
                categoryToEdit.setCategoryDes(newDes);
            }
            System.out.println("Mời nhập vào trạng thái của danh mục");
            boolean status = getBoolean();

            categoryToEdit.setCategoryStatus(status);
            categoryService.save(categoryToEdit);
        } else {
            printlnError("Không tìm thấy mã danh mục cần sửa !!!");
        }
    }

    private void searchCategoryByName() {
        System.out.println("Nhập tên danh mục muốn tìm kiếm");
        String searchName = getString();
        List<Category> categories = categoryService.findAll();
        List<Category> category = new ArrayList<>();
        boolean flag = false;
        for (Category cate: categories) {
            if (cate.getCategoryName().contains(searchName.trim())) {
                category.add(cate);
                flag = true;
            }
        }
        if (flag) {
            System.out.println("Danh sách danh mục: ");
            System.out.println("---------------------------------------------------------------------");
            System.out.println(String.format("|%-30s|%-30s|%-30s", "Mã danh mục", "Tên danh mục", "Trạng thái"));
            System.out.println("---------------------------------------------------------------------");
            for (Category catalog: category) {

                catalog.displayCategory();
            }
        } else {
            System.err.println("Không tìm thấy danh mục phù hợp");
        }
    }

    private void displayAllCategorys() {
        List<Category> categories = categoryService.findAll();
        if (categories.isEmpty()) {
            System.err.println("Danh sách Category rỗng");
        } else {
            System.out.println("Danh sách Category");
            System.out.println("---------------------------------------------------------------------");
            System.out.println(String.format("|%-30s|%-30s|%-30s", "Mã danh mục", "Tên danh mục", "Trạng thái"));
            System.out.println("---------------------------------------------------------------------");

            for (Category category : categories) {
                category.displayCategory();
            }
        }
    }
//    private  void sortByteName (){
//        List<Category> categories = categoryService.findAll();
//        categories.sort();
//
//    }

    private void addCategory() {
        System.out.println("Nhập số danh mục cần thm mới");
        int numberOfCategories = getInteger();
        if (numberOfCategories <= 0) {
            printlnError("Số danh mục phải lớn hơn 0");
            return;
        }
        for (int i = 0; i < numberOfCategories; i++) {
            List<Category> categories = categoryService.findAll();
            System.out.println("Danh mục thứ " + (i + 1));
            Category category = new Category();

            // Nhập tên danh mục và kiểm tra xem tên đã tồn tại chưa
            while (true) {
                System.out.println("Nhập tên danh mục");
                String categoryName = getString();
                boolean isNameExists = false;
                for (Category cate : categories) {
                    if (cate.getCategoryName().equalsIgnoreCase(categoryName)) {
                        isNameExists = true;
                        System.err.println("Tên danh mục đã tồn tại, mời nhập tên mới.");
                        break;
                    }
                }
                if (!isNameExists) {
                    category.setCategoryName(categoryName);
                    break; // Kết thúc vòng lặp khi tên hợp lệ và không trùng lặp
                }
            }
            System.out.println("Nhập mô tả danh mục:");
            String categoryDes = getString();
            category.setCategoryDes(categoryDes);
            System.out.println("Nhập trạng thái danh mục");
            boolean status = getBoolean();
            category.setCategoryStatus(status);

            category.setId(categoryService.autoInc());
            categoryService.save(category);

        }
        System.out.println("Tạo category thành công");
    }


}

