import javax.swing.*;
import javax.swing.plaf.basic.BasicArrowButton;
import java.awt.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.prefs.Preferences;

/**
 * This is the window for WinRateCalc.  Everything happens in this class and its inner classes.
 */
public class WinRateWindow extends JFrame
{
  //variable declaration
  private final boolean VICTORY = true;
  private final boolean DEFEAT = false;
  private static Toolkit tk = Toolkit.getDefaultToolkit();
  private static Dimension screen = tk.getScreenSize();
  private JMenuBar toolbar;
  private Preferences savedRates;
  private static Font rateFont = new Font("Calibri", Font.BOLD, screen.height / 10);
  private static Font txtFont = new Font("Calibri", Font.BOLD, screen.height / 50);
  private JButton clear;
  private VicDefLabel winLabel;
  private VicDefLabel lossLabel;
  private static Color PERFECT = new Color(0, 132, 6);
  private static Color GREAT = new Color(19, 193, 56);
  private static Color GOOD = new Color(66, 244, 89);
  private static Color OKAY = new Color(221, 242, 60);
  private static Color NOTGOOD = new Color(255, 102, 0);
  private static Color BAD = new Color(255, 38, 0);
  private static Color AWFUL = Color.red;
  private static Color STOP = Color.black;
  private int victories;
  private int losses;
  private JLabel winRate;
  private double percent = 100;


  /**
   * Default constructor.
   */
  public WinRateWindow()
  {
    try
    {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    } catch (Exception e)
    {
      e.printStackTrace();
    }
    savedRates = Preferences.userRoot();
    victories = savedRates.getInt("WinRateCalcVICTORIES", 0);
    losses = savedRates.getInt("WinRateCalcLOSSES", 0);

    setupMenubar();
    setJMenuBar(toolbar);
    setTitle("WinRate Calc");
    URL url = ClassLoader.getSystemResource("percent.png");
    Image ico = tk.createImage(url);
    setIconImage(ico);
    setSize((int)(screen.width / 3.5), (int)(screen.height / 2.4));
    setResizable(false);
    setLayout(new GridLayout(2, 5));

    winRate = winRate();
    updateRate();


    add(winRate);
    add(buttonArea());


    setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    setVisible(true);
  }

  /**
   * Update the win% either when a button is pressed or when the app starts.
   */
  private void updateRate()
  {
    if (victories + losses > 0)
    {
      percent = Math.min((((double)victories / (losses + victories)) *
        100), 100.0);
    } else
    {
      percent = 100;
    }

    winRate.setText(String.format("%.2f%%", percent));
    if (percent == 100)
    {
      winRate.setBackground(PERFECT);
    } else if (percent >= 95)
    {
      winRate.setBackground(GREAT);

    } else if (percent >= 90)
    {
      winRate.setBackground(GOOD);
    } else if (percent >= 80)
    {
      winRate.setBackground(OKAY);
    } else if (percent >= 75)
    {
      winRate.setBackground(NOTGOOD);

    } else if (percent >= 60)
    {
      winRate.setBackground(BAD);

    } else if (percent >= 50)
    {
      winRate.setBackground(AWFUL);
    } else
    {
      winRate.setBackground(STOP);
    }



    savedRates.putInt("WinRateCalcVICTORIES", victories);
    savedRates.putInt("WinRateCalcLOSSES", losses);
    savedRates.putDouble("WinRateCalcWINRATE", percent);


  }

  /**
   * Setup the menubar.
   */
  private void setupMenubar()
  {
    toolbar = new JMenuBar();
    clear = new JButton("Clear");
    clear.addActionListener(event ->
    {
      savedRates.putInt("WinRateCalcVICTORIES", 0);
      savedRates.putInt("WinRateCalcLOSSES", 0);
      savedRates.putDouble("WinRateCalcWINRATE", 100);
      reset();
    });
    toolbar.add(clear);
  }

  /**
   * Reset when clear is pressed.
   */
  private void reset()
  {
    victories = savedRates.getInt("WinRateCalcVICTORIES", 0);
    losses = savedRates.getInt("WinRateCalcLOSSES", 0);
    lossLabel.reset();
    winLabel.reset();
    updateRate();
  }

  /**
   * Create the winRate label.
   *
   * @return the winRate label.
   */
  private JLabel winRate()
  {
    JLabel winRate = new JLabel(String.format("%.1f%%", savedRates.getDouble
      ("WinRateCalcWINRATE", 100)));
    winRate.setFont(rateFont);
    winRate.setHorizontalAlignment(JLabel.CENTER);
    winRate.setVerticalAlignment(JLabel.CENTER);
    winRate.setForeground(Color.white);
    winRate.setOpaque(true);
    winRate.setBackground(PERFECT);
    return winRate;
  }

  /**
   * Create the button area.
   *
   * @return the button area
   */
  private JPanel buttonArea()
  {
    JPanel buttonArea = new JPanel();
    ArrayList<VicDefLabel> labels = new ArrayList<>();

    JButton upButton = new BasicArrowButton(SwingConstants.NORTH);
    JButton downButton = new BasicArrowButton(SwingConstants.SOUTH);
    winLabel = new VicDefLabel(VICTORY, savedRates.getInt("WinRateCalcVICTORIES", 0));
    lossLabel = new VicDefLabel(DEFEAT, savedRates.getInt("WinRateCalcLOSSES", 0));
    labels.add(winLabel);
    labels.add(lossLabel);

    buttonArea.setLayout(new GridLayout());
    buttonArea.add(upButton);

    for (VicDefLabel cur: labels)
    {
      cur.setHorizontalAlignment(JLabel.CENTER);
      cur.setVerticalAlignment(JLabel.CENTER);
      cur.setFont(txtFont);
      buttonArea.add(cur);
    }

    upButton.addActionListener(event ->
    {
      victories++;
      winLabel.update();
      updateRate();
    });

    downButton.addActionListener(event ->
    {
      losses++;
      lossLabel.update();
      updateRate();
    });

    buttonArea.add(downButton);
    return buttonArea;
  }

  /**
   * A class that represents a victory label or defeat label. Used to make keeping the labels up
   * to date easier.
   */
  private class VicDefLabel extends JLabel
  {

    boolean type;
    int count;

    /**
     * 1-arg constructor, just asks for type (victory or defeat). (No longer used).
     *
     * @param type which type this label is
     */
    public VicDefLabel(boolean type)
    {
      this.type = type;
      count = 0;

      if (type == VICTORY)
      {
        this.setText("Victory: 0");
      } else
      {
        this.setText("Defeat: 0");
      }
    }

    /**
     * 2-arg constructor.
     *
     * @param type type of this label
     * @param value initial value of this label
     */
    public VicDefLabel(boolean type, int value)
    {
      this.type = type;
      count = value;

      if (type == VICTORY)
      {
        this.setText("Victory: " + count);

      } else
      {
        this.setText("Defeat: " + count);
      }
    }

    /**
     * Increment this label.
     */
    public void update()
    {
      count++;

      if (type)
      {
        this.setText("Victory: " + count);
      } else
      {
        this.setText("Defeat: " + count);
      }
    }

    /**
     * Reset this label to 0.
     */
    public void reset()
    {
      count = -1;
      update();
    }

  }

}
