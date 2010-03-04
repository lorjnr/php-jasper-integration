package ch.bayo.jasper.cockpit.ui;

import java.awt.event.*;

public class WindowClosingAdapter extends WindowAdapter
{
  private boolean exitSystem;

  public WindowClosingAdapter(boolean exitSystem)
  {
    this.exitSystem = exitSystem;
  }
  
  public WindowClosingAdapter()
  {
    this(true);
  }
  
  public void windowClosing(WindowEvent event)
  {
    event.getWindow().setVisible(false);
    event.getWindow().dispose();
    BaseFrame frm = (BaseFrame)(event.getWindow());
    frm.doAfterClose();
    if (exitSystem) {
      System.exit(0);
    }
  }
}
