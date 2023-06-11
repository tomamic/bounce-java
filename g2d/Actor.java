package g2d;

public interface Actor {
  public void move(Arena arena);
  public Pt pos();
  public Pt size();
  public Pt sprite();
}
