package g2d;

import java.util.Collections;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Arena {
  Pt size;
  int count = 0;
  int turn = -1;
  Set<String> currKeys = new HashSet<>();
  Set<String> prevKeys = new HashSet<>();
  ArrayList<Actor> actors = new ArrayList<>();
  ArrayList<ArrayList<Actor>> collisions = new ArrayList<>();

  public static boolean checkCollision(Actor a1, Actor a2) {
    Pt p1 = a1.pos(), s1 = a1.size(), p2 = a2.pos(), s2 = a2.size();
    return (p2.y() <= p1.y() + s1.y() && p1.y() <= p2.y() + s2.y() &&
            p2.x() <= p1.x() + s1.x() && p1.x() <= p2.x() + s2.x());

  }

  public Arena(Pt size) {
    this.size = size;
  }

  public void spawn(Actor a) {
    if (! actors.contains(a)) {
      actors.add(a);
    }
  }

  public void kill(Actor a) {
    if (actors.contains(a)) {
      actors.remove(a);
    }
  }

  void detectCollisions(ArrayList<Actor> actors) {
    collisions.clear();
    for (var a1 : actors) {
      var coll1 = new ArrayList<Actor>();
      for (var a2 : actors) {
        if (a1 != a2 && checkCollision(a1, a2)) {
          coll1.add(a2);
        }
      }
      collisions.add(coll1);
    }
  }

  public void tick(Set<String> keys) {
    var actors = new ArrayList<Actor>(this.actors);
    detectCollisions(actors);
    prevKeys = new HashSet<String>(currKeys);
    currKeys = keys;
    turn = -1;
    for (var a : actors) {
      turn += 1;
      a.move(this);
    }
    count += 1;
  }

  public List<Actor> collisions() {
    return (0 <= turn && turn < collisions.size()) ? collisions.get(turn) : new ArrayList<Actor>();
  }

  public List<Actor> actors() {
    return Collections.unmodifiableList(actors);
  }

  public Pt size() {
    return size;
  }

  public int count() {
    return count;
  }

  public Set<String> currentKeys() {
    return Collections.unmodifiableSet(currKeys);
  }

  public Set<String> previousKeys() {
    return Collections.unmodifiableSet(prevKeys);
  }
}
