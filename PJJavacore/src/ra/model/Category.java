package ra.model;

public class Category extends Entity {


    private String categoryName;
    private String categoryDes;
    private boolean categoryStatus;

    public Category() {

    }

    public Category(Integer id, String categoryName, String categoryDes, boolean categoryStatus) {
        this.id = id;
        this.categoryName = categoryName;
        this.categoryDes = categoryDes;
        this.categoryStatus = categoryStatus;
    }



    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getCategoryDes() {
        return categoryDes;
    }

    public void setCategoryDes(String categoryDes) {
        this.categoryDes = categoryDes;
    }


    public void setCategoryStatus(boolean categoryStatus) {
        this.categoryStatus = categoryStatus;
    }
    public  void displayCategory() {


        System.out.println(String.format("|%-30d|%-30s|%-30s", this.id, this.categoryName, (this.categoryStatus ? "Hiện" : "Ẩn")));
        System.out.println("---------------------------------------------------------------------");



    }
}
