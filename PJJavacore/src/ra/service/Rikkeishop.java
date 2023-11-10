package ra.service;


import java.util.List;

public interface Rikkeishop<T>{
    void save(T t);

    List<T> findAll();

    T findById(int id);

    int findIndex(int id);

    int autoInc();

}
