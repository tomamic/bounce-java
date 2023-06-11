package g2d;

//import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

public class Canvas extends JPanel implements ActionListener, MouseListener, MouseMotionListener {
  JFrame frame;
  BufferedImage buffer;
  Timer timer;
  Runnable runnable;
  Pt size;
  Pt mouse;
  HashSet<String> currKeys = new HashSet<>();
  HashSet<String> prevKeys = new HashSet<>();
  Color color = new Color(127, 127, 127);
  HashMap<String, Image> images = new HashMap<>();
  HashMap<String, Clip> audios = new HashMap<>();

  public Canvas(Pt size) {
    this.size = size;
    buffer = new BufferedImage(size.x(), size.y(), BufferedImage.TYPE_INT_ARGB);
    addKeyListener(new KeyAdapter() {
      @Override
      public void keyPressed(KeyEvent e) {
        currKeys.add(keyName(e));
      }

      @Override
      public void keyReleased(KeyEvent e) {
        //System.out.println(keyName(e));
        currKeys.remove(keyName(e));
      }
    });
    addMouseListener(this);
    addMouseMotionListener(this);
    setFocusable(true);
    SwingUtilities.invokeLater(() -> {
      frame = new JFrame("Arena");
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      frame.add(Canvas.this);
      frame.pack();
      frame.setResizable(false);
      frame.setVisible(true);
    });
  }

  @Override
  public Dimension getPreferredSize() {
    return new Dimension(size.x(), size.y());
  }

  @Override
  public void paintComponent(Graphics g) {
    super.paintComponent(g);
    g.drawImage(buffer, 0, 0, null);
  }

  public void setColor(Color color) {
    this.color = color;
  }

  public void setColor(int r, int g, int b) {
    this.color = new Color(r, g, b);
  }

  public void drawRect(Pt pos, Pt size) {
    Graphics g = buffer.getGraphics();
    g.setColor(new java.awt.Color(color.r(), color.g(), color.b()));
    g.fillRect(pos.x(), pos.y(), size.x(), size.y());
    g.dispose();
  }

  public void drawCircle(Pt center, int radius) {
    Graphics g = buffer.getGraphics();
    g.setColor(new java.awt.Color(color.r(), color.g(), color.b()));
    g.fillOval(center.x() - radius, center.y() - radius, radius * 2, radius * 2);
    g.dispose();
  }

  public void clear() {
    Graphics g = buffer.getGraphics();
    g.setColor(java.awt.Color.white);
    g.fillRect(0, 0, size.x(), size.y());
  }

  public void update() {
    prevKeys = new HashSet<>(currKeys);
    repaint();
  }

  /*
  def update_canvas() -> None:
      global _prev_keys
      _prev_keys = set(_curr_keys)
      if _canvas is not _display:
          scaled = pg.transform.scale(_canvas, _display.get_size())
          _display.blit(scaled, (0, 0))
      pg.display.update()
  */

  public void drawLine(Pt pt1, Pt pt2) {
    Graphics g = buffer.getGraphics();
    g.setColor(new java.awt.Color(color.r(), color.g(), color.b()));
    g.drawLine(pt1.x(), pt1.y(), pt2.x(), pt2.y());
    g.dispose();
  }

  public void drawText(String txt, Pt pos, int size) {
    var g = buffer.getGraphics();
    g.setColor(new java.awt.Color(color.r(), color.g(), color.b()));
    var ppp = 72f / Toolkit.getDefaultToolkit().getScreenResolution();
    g.setFont(g.getFont().deriveFont(ppp * size));
    var metrics = g.getFontMetrics(g.getFont());
    g.drawString(txt, pos.x(), pos.y() + metrics.getAscent());
    g.dispose();
  }

  public void drawTextCentered(String txt, Pt pos, int size) {
    var g = buffer.getGraphics();
    g.setColor(new java.awt.Color(color.r(), color.g(), color.b()));
    var ppp = 72f / Toolkit.getDefaultToolkit().getScreenResolution();
    g.setFont(g.getFont().deriveFont(ppp * size));
    var metrics = g.getFontMetrics(g.getFont());
    int x = pos.x() - (int)(metrics.stringWidth(txt) / 2);
    int y = pos.y() - metrics.getHeight() / 2 + metrics.getAscent();
    g.drawString(txt, x, y);
    g.dispose();
  }

  public String loadImage(String src) {
    if (!images.containsKey(src)) {
      try {
        images.put(src, ImageIO.read(getClass().getResource(src)));
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    return src;
  }

  public void drawImage(String src, Pt pos) {
    var g = buffer.getGraphics();
    g.drawImage(images.get(loadImage(src)), pos.x(), pos.y(), null);
    g.dispose();
  }

  public void drawImageClip(String src, Pt pos, Pt clipPos, Pt clipSize) {
    var g = buffer.getGraphics();
    g.drawImage(images.get(loadImage(src)), pos.x(), pos.y(), clipSize.x(), clipSize.y(), clipPos.x(), clipPos.y(), clipSize.x(), clipSize.y(), null);
    g.dispose();
  }

  public String loadAudio(String src) {
    if (!audios.containsKey(src)) {
      try {
        Clip clip = AudioSystem.getClip();
        AudioInputStream inputStream = AudioSystem.getAudioInputStream(getClass().getResourceAsStream(src));
        clip.open(inputStream);
        audios.put(src, clip);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    return src;
  }

  public void playAudio(String src) {
    var clip = audios.get(loadAudio(src));
    clip.setFramePosition(0);
    clip.start();
  }

  public void pauseAudio(String src) {
    var clip = audios.get(loadAudio(src));
    clip.stop();
  }

  public void alert(String message) {
    update();
    JOptionPane.showMessageDialog(this, "Eggs are not supposed to be green.");
  }

  public boolean confirm(String message) {
    update();
    return JOptionPane.showConfirmDialog(this, message) == JOptionPane.YES_OPTION;
  }

  public String prompt(String message) {
    update();
    var ans = JOptionPane.showInputDialog(this, message);
    return ans == null ? "" : ans;
  }

  public Pt mousePos() {
    return mouse;
  }

  public Set<String> currentKeys() {
    return Collections.unmodifiableSet(currKeys);
  }

  public Set<String> previousKeys() {
    return Collections.unmodifiableSet(prevKeys);
  }

  public boolean keyPressed(String key) {
    return currKeys.contains(key) && ! prevKeys.contains(key);
  }

  public boolean keyReleased(String key) {
    return prevKeys.contains(key) && ! currKeys.contains(key);
  }

  public boolean mouseClicked() {
    return keyReleased("LeftButton");
  }

  public boolean mouseRightClicked() {
    return keyReleased("RightButton");
  }

  public void close() {
    frame.setVisible(false);
    frame.dispose();
    System.exit(0);
  }

  public void mainLoop(Runnable runnable, int fps) {
    this.update();
    this.runnable = runnable;
    timer = new Timer(1000 / fps, this);
    timer.start();
  }

  public void mainLoop(Runnable runnable) {
    mainLoop(runnable, 30);
  }

  public void mainLoop() {
    mainLoop(() -> {}, 30);
  }

  /*
  def main_loop(tick=None, fps=30) -> None:
      global _mouse_pos, _tick
      _tick = tick
      clock = pg.time.Clock()
      update_canvas()
      running = True
      while running:
          for e in pg.event.get():
              if e.type == pg.QUIT:
                  running = False
                  break
              elif e.type == pg.KEYDOWN:
                  _curr_keys.add(_kb_name(e.key))
              elif e.type == pg.KEYUP:
                  _curr_keys.discard(_kb_name(e.key))
              elif e.type == pg.MOUSEBUTTONDOWN:
                  _curr_keys.add(_mb_name(e.button))
              elif e.type == pg.MOUSEBUTTONUP:
                  _curr_keys.discard(_mb_name(e.button))
          if _tick:
              _mouse_pos = pg.mouse.get_pos()
              _tick()
              update_canvas()
          clock.tick(fps)
      close_canvas()

  */

  String keyName(KeyEvent e) {
    var name = KeyEvent.getKeyText(e.getKeyCode()).replace(" ", "");
    if (name.length() == 1) {
      name = name.toLowerCase();
    } else if (Set.of("Up", "Down", "Left", "Right").contains(name)) {
      name = "Arrow" + name;
    } else if (name.equals("Space")) {
      name = "Spacebar";
    } else if (name.equals("Ctrl")) {
      name = "Control";
    }
    return name;
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    if (runnable != null) {
      runnable.run();
    }
    update();
  }

  @Override
  public void mouseClicked(MouseEvent e) {}

  @Override
  public void mouseEntered(MouseEvent e) {}

  @Override
  public void mouseExited(MouseEvent e) {}

  @Override
  public void mousePressed(MouseEvent e) {
    if (e.getButton() == java.awt.event.MouseEvent.BUTTON1) {
      currKeys.add("LeftButton");
    } else if (e.getButton() == java.awt.event.MouseEvent.BUTTON2) {
      currKeys.add("MiddleButton");
    } else if (e.getButton() == java.awt.event.MouseEvent.BUTTON3) {
      currKeys.add("RightButton");
    }
  }
  public void mouseReleased(MouseEvent e) {
    if (e.getButton() == java.awt.event.MouseEvent.BUTTON1) {
      currKeys.remove("LeftButton");
    } else if (e.getButton() == java.awt.event.MouseEvent.BUTTON2) {
      currKeys.remove("MiddleButton");
    } else if (e.getButton() == java.awt.event.MouseEvent.BUTTON3) {
      currKeys.remove("RightButton");
    }
  }

  public void mouseMoved(MouseEvent e) {
    mouse = new Pt(e.getX(), e.getY());
  }

  public void mouseDragged(MouseEvent e) {
    mouse = new Pt(e.getX(), e.getY());
  }
}
