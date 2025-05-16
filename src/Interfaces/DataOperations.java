package Interfaces;

import java.util.List;

public interface DataOperations<T> {
    void saveToFile(String filePath, List<T> data);
    List<T> loadFromFile(String filePath);
}
