import g2d.Actor;
import g2d.Arena;
import g2d.Canvas;
import g2d.Color;
import g2d.Pt;

public class Main {
  static class Ball implements Actor {
    Pt size = new Pt(20, 20);
    int x, y;
    int speed = 5, dx = 5, dy = 5;
    public Ball(Pt pos) { x = pos.x(); y = pos.y(); }
    public void move(Arena arena) {
      for (var other : arena.collisions()) {
        if (true) { //! other instanceof Ghost) {
          dx = (other.pos().x() < x) ? speed : -speed;
          dy = (other.pos().y() < y) ? speed : -speed;
        }
      }
      int x1 = x + dx, xmax = arena.size().x() - size.x();
      int y1 = y + dy, ymax = arena.size().y() - size.y();
      dx = x1 < 0 ? speed : x1 > xmax ? -speed : dx;
      dy = y1 < 0 ? speed : y1 > ymax ? -speed : dy;
      x += dx;
      y += dy;
    }
    public Pt pos() { return new Pt(x, y); }
    public Pt size() { return size; }
    public Pt sprite() { return new Pt(0, 0); }
  }
  static Arena arena = new Arena(new Pt(320, 240));
  static Canvas canvas = new Canvas(arena.size());

  public static void tick() {
    canvas.clear();
    canvas.setColor(new Color(255, 0, 0));
    for (var a : arena.actors()) {
      canvas.drawRect(a.pos(), a.size());
    }
    arena.tick(canvas.currentKeys());
    Pt pos = canvas.mousePos();
    if (canvas.mouseClicked() && pos.x() < 100 && pos.y() < 100) {
      canvas.close();
    }
  }

  public static void main(String[] args) {
    arena.spawn(new Ball(new Pt(140, 100)));
    arena.spawn(new Ball(new Pt(100, 140)));
    arena.spawn(new Ball(new Pt(180, 120)));
    canvas.setColor(new Color(10, 10, 100));
    canvas.drawRect(new Pt(10, 10), new Pt(100, 100));
    canvas.setColor(new Color(100, 10, 10));
    canvas.drawCircle(new Pt(320, 240), 10);
    canvas.drawText("Ciao, mondo!", new Pt(10, 10), 50);
    canvas.mainLoop(Main::tick);
  }
}
