package uni.minesweeper.database;

public interface Callback<T> {
  void call(final T data);
}
