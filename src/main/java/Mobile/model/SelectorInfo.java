package Mobile.model;

import org.openqa.selenium.By;

public class SelectorInfo {

  private By by;
  private final int index;

  public SelectorInfo(By by, int index) {
    this.by = by;
    this.index = index;
  }

  public By getBy() {
    return by;
  }

  public void setBy(By by) {
    this.by = by;
  }

  public int getIndex() {
    return index;
  }

}
