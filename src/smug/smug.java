/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package smug;
import javax.swing.SwingUtilities;

/**
 *
 * @author vktgz
 */
public class smug
{
  public static void main(String args[])
  {
    SwingUtilities.invokeLater(new Runnable()
    {
      @Override
      public void run()
      {
        rlApp app = new rlApp();
        app.run();
      }
    });
  }
}
