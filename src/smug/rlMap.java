/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package smug;
import java.util.ArrayList;
import java.util.Random;

/**
 *
 * @author vktgz
 */
public class rlMap
{
  final private int MIN_ROOMS = 3;
  final private int MAX_ROOMS = 7;
  final private int MIN_ROOM_SIZE = 5;
  final private int MAX_ROOM_SIZE = 9;
  final private int MIN_DOORS = 1;
  final private int MAX_DOORS = 2;

  private ArrayList<ArrayList<rlObj>> map;
  private int cols;
  private int rows;
  private ArrayList<rlDoor> doors;
  private ArrayList<rlObj> graf;

  public rlMap(int ncols, int nrows)
  {
    cols = ncols;
    rows = nrows;
    doors = new ArrayList<rlDoor>();
    graf = new ArrayList<rlObj>();
    map = new ArrayList<ArrayList<rlObj>>(rows);
    cleanMap();
    generateMap();
  }

  private void cleanMap()
  {
    ArrayList<rlObj> tmp;
    doors.clear();
    graf.clear();
    map.clear();
    for (int r = 0; r < rows; r++)
    {
      tmp = new ArrayList<rlObj>(cols);
      for (int c = 0; c < cols; c++)
      {
        if ((r == 0) || (r == (rows - 1)) || (c == 0) || (c == (cols - 1)))
          tmp.add(new rlWall(rlWall.Kind.SIDE));
        else
          tmp.add(new rlWall(rlWall.Kind.ROCK));
      }
      map.add(r, tmp);
    }
  }

  private void generateMap()
  {
    Random gen = new Random();
    boolean done = false;
    while (!done)
    {
      System.out.println("Generating map...");
      cleanMap();
      int rs = MIN_ROOMS + gen.nextInt(MAX_ROOMS - MIN_ROOMS + 1);
      for (int i = 0; i < rs; i++)
        generateRoom();
      done = generateTunnels();
    }
  }

  private void generateRoom()
  {
    Random gen = new Random();
    boolean done = false;
    while (!done)
    {
      System.out.println("Generating room...");
      done = true;
      int x = gen.nextInt(cols - MAX_ROOM_SIZE);
      int y = gen.nextInt(rows - MAX_ROOM_SIZE);
      int w = MIN_ROOM_SIZE + gen.nextInt(MAX_ROOM_SIZE - MIN_ROOM_SIZE + 1);
      int h = MIN_ROOM_SIZE + gen.nextInt(MAX_ROOM_SIZE - MIN_ROOM_SIZE + 1);
      w = x + w - 1;
      h = y + h - 1;
      for (int cx = x; cx <= w; cx++)
        for (int cy = y; cy <= h; cy++)
        {
          if (((cx == x) || (cx == w)) && ((cy == y) || (cy == h)))
            done = done & checkCorner(map.get(cy).get(cx));
          if ((((cx == x) || (cx == w)) && (cy != y) && (cy != h)) || (((cy == y) || (cy == h)) && (cx != x) && (cx != w)))
            done = done & checkWall(map.get(cy).get(cx));
          if ((cx != x) && (cx != w) && (cy != y) && (cy != h))
            done = done & checkFloor(map.get(cy).get(cx));
        }
      if (done)
      {
        rlObj o;
        rlWall ow;
        for (int cx = x; cx <= w; cx++)
          for (int cy = y; cy <= h; cy++)
          {
            if (((cx == x) || (cx == w)) && ((cy == y) || (cy == h)))
            {
              ow = (rlWall)map.get(cy).get(cx);
              if ((ow.kind == rlWall.Kind.ROCK) || (ow.kind == rlWall.Kind.WALL))
                ow.kind = rlWall.Kind.CORN;
            }
            if ((((cx == x) || (cx == w)) && (cy != y) && (cy != h)) || (((cy == y) || (cy == h)) && (cx != x) && (cx != w)))
            {
              o = map.get(cy).get(cx);
              if (o instanceof rlWall)
              {
                ow = (rlWall)o;
                if (ow.kind == rlWall.Kind.ROCK)
                  ow.kind = rlWall.Kind.WALL;
              }
            }
            if ((cx != x) && (cx != w) && (cy != y) && (cy != h))
              map.get(cy).set(cx, new rlFloor(rlFloor.Kind.ROOM, cx, cy));
          }
        int ds = MIN_DOORS + gen.nextInt(MAX_DOORS - MIN_DOORS + 1);
        for (int i = 0; i < ds; i++)
          generateDoor(i, x, y, w, h);
      }
    }
  }

  private boolean checkCorner(rlObj w)
  {
    return (w instanceof rlWall);
  }

  private boolean checkWall(rlObj w)
  {
    return ((w instanceof rlWall) || (w instanceof rlDoor));
  }

  private boolean checkFloor(rlObj w)
  {
    boolean res = w instanceof rlWall;
    if (res)
      res = ((rlWall)w).kind == rlWall.Kind.ROCK;
    return res;
  }

  private boolean checkDoor(rlObj o)
  {
    boolean res = o instanceof rlFloor;
    if (!res)
    {
      res = o instanceof rlWall;
      if (res)
        res = ((rlWall)o).kind == rlWall.Kind.ROCK;
    }
    return res;
  }

  private void generateDoor(int idx, int rx, int ry, int rw, int rh)
  {
    Random gen = new Random();
    boolean done = false;
    while (!done)
    {
      System.out.println("Generating door...");
      int x = rx + gen.nextInt(rw - rx + 1);
      int y = ry + gen.nextInt(rh - ry + 1);
      rlObj o = map.get(y).get(x);
      if (o instanceof rlWall)
      {
        rlWall w = (rlWall)o;
        if (w.kind == rlWall.Kind.WALL)
        {
          rlDoor d = null;
          if (y == ry)
            if (checkDoor(map.get(y - 1).get(x)))
              d = new rlDoor(idx, rlDoor.Dir.N, x, y);
          if (y == rh)
            if (checkDoor(map.get(y + 1).get(x)))
              d = new rlDoor(idx, rlDoor.Dir.S, x, y);
          if (x == rx)
            if (checkDoor(map.get(y).get(x - 1)))
              d = new rlDoor(idx, rlDoor.Dir.W, x, y);
          if (x == rw)
            if (checkDoor(map.get(y).get(x + 1)))
              d = new rlDoor(idx, rlDoor.Dir.E, x, y);
          if (d != null)
          {
            map.get(y).set(x, d);
            doors.add(d);
            if ((y == ry) || (y == rh))
            {
              w = (rlWall)map.get(y).get(x + 1);
              if (w.kind != rlWall.Kind.SIDE)
                w.kind = rlWall.Kind.CORN;
              w = (rlWall)map.get(y).get(x - 1);
              if (w.kind != rlWall.Kind.SIDE)
                w.kind = rlWall.Kind.CORN;
            }
            if ((x == rx) || (x == rw))
            {
              w = (rlWall)map.get(y + 1).get(x);
              if (w.kind != rlWall.Kind.SIDE)
                w.kind = rlWall.Kind.CORN;
              w = (rlWall)map.get(y - 1).get(x);
              if (w.kind != rlWall.Kind.SIDE)
                w.kind = rlWall.Kind.CORN;
            }
            done = true;
          }
        }
      }
    }
  }

  void fillBuf(rlBuffer buf)
  {
    for (int x = 0; x < cols; x++)
      for (int y = 0; y < rows; y++)
        buf.put(x + 1, y + 1, map.get(y).get(x).getSymbol());
  }

  private boolean generateTunnels()
  {
    rlObj from;
    boolean done = false;
    boolean res = true;
    int step = 0;
    while (!done)
    {
      step++;
      System.out.println("Generating tunnel...");
      buildGraf();
      from = null;
      for (int i = 0; i < doors.size(); i++)
      {
        from = doors.get(i);
        if (graf.contains(from))
          from = null;
        else
          break;
      }
      done = (from == null);
      if (!done)
      {
        res = digTunnel(from);
        if (!res)
          done = true;
      }
      if (step > 20)
      {
        System.out.println("Loop break");
        done = true;
      }
    }
    return res;
  }

  private rlObj findTarget(int x, int y)
  {
    rlObj to, tmp;
    int diff, ndiff;
    to = graf.get(0);
    diff = (Math.abs(x - to.x) + Math.abs(y - to.y));
    for (int i = 1; i < graf.size(); i++)
    {
      tmp = graf.get(i);
      if (tmp instanceof rlFloor)
        if (((rlFloor) tmp).kind == rlFloor.Kind.ROOM)
          continue;
      ndiff = (Math.abs(x - tmp.x) + Math.abs(y - tmp.y));
      if (ndiff < diff)
        to = tmp;
    }
    return to;
  }

  private boolean digTunnel(rlObj from)
  {
    Random gen = new Random();
    boolean done = false;
    boolean res = true;
    int x = from.x;
    int y = from.y;
    int step = 0, pass = 0;
    rlDoor.Dir d = rlDoor.Dir.N;
    if (from instanceof rlDoor)
      d = ((rlDoor)from).dir;
    rlDoor.Dir nd = d;
    rlObj to, o;
    int nx, ny, dh, dw;
    boolean turn = false;
    System.out.println("Digging tunnel...");
//    to = findTarget(x, y);
    while (!done)
    {
      pass++;
      if ((pass % 10) == 0)
        System.out.println("...pass: " + Integer.toString(pass));
      if ((step % 10) == 0)
        System.out.println("...step: " + Integer.toString(step));
      if (step > 1000)
      {
        done = true;
        res = false;
        continue;
      }
      to = findTarget(x, y);
      if (step > 0)
      {
        if ((step > 1) && (!turn))
        {
          dh = Math.abs(to.x - x);
          dw = Math.abs(to.y - y);
          if (dw < dh)
          {
            if (to.y > y)
              nd = rlDoor.Dir.S;
            else
              nd = rlDoor.Dir.N;
          }
          else
          {
            if (to.x > x)
              nd = rlDoor.Dir.E;
            else
              nd = rlDoor.Dir.W;
          }
          if (((d == rlDoor.Dir.N) && (nd == rlDoor.Dir.S)) || ((d == rlDoor.Dir.S) && (nd == rlDoor.Dir.N)))
          {
            if (to.x > x)
              nd = rlDoor.Dir.E;
            else
              nd = rlDoor.Dir.W;
          }
          if (((d == rlDoor.Dir.W) && (nd == rlDoor.Dir.E)) || ((d == rlDoor.Dir.E) && (nd == rlDoor.Dir.W)))
          {
            if (to.y > y)
              nd = rlDoor.Dir.S;
            else
              nd = rlDoor.Dir.N;
          }
//          if (((d == rlDoor.Dir.N) && (nd == rlDoor.Dir.S)) || ((d == rlDoor.Dir.S) && (nd == rlDoor.Dir.N)) || ((d == rlDoor.Dir.W) && (nd == rlDoor.Dir.E)) || ((d == rlDoor.Dir.E) && (nd == rlDoor.Dir.W)))
//            turn = true;
        }
        if (turn)
        {
          if (nd == rlDoor.Dir.N)
            nd = rlDoor.Dir.E;
          else if (nd == rlDoor.Dir.E)
            nd = rlDoor.Dir.S;
          else if (nd == rlDoor.Dir.S)
            nd = rlDoor.Dir.W;
          else if (nd == rlDoor.Dir.W)
            nd = rlDoor.Dir.N;
/*          boolean lr = gen.nextBoolean();
          if (nd == rlDoor.Dir.N)
          {
            if (lr)
              nd = rlDoor.Dir.E;
            else
              nd = rlDoor.Dir.W;
          }
          else if (nd == rlDoor.Dir.E)
          {
            if (lr)
              nd = rlDoor.Dir.S;
            else
              nd = rlDoor.Dir.N;
          }
          else if (nd == rlDoor.Dir.S)
          {
            if (lr)
              nd = rlDoor.Dir.W;
            else
              nd = rlDoor.Dir.E;
          }
          else if (nd == rlDoor.Dir.W)
          {
            if (lr)
              nd = rlDoor.Dir.N;
            else
              nd = rlDoor.Dir.S;
          } */
        }
        d = nd;
      }
      nx = x;
      ny = y;
      turn = false;
      if (d == rlDoor.Dir.N)
        ny = y - 1;
      if (d == rlDoor.Dir.S)
        ny = y + 1;
      if (d == rlDoor.Dir.W)
        nx = x - 1;
      if (d == rlDoor.Dir.E)
        nx = x + 1;
      o = map.get(ny).get(nx);
      if (o instanceof rlDoor)
      {
        rlDoor tmp = (rlDoor)o;
        if ((tmp.x == to.x) && (tmp.y == to.y))
          done = true;
        else
          turn = true;
      }
      if (o instanceof rlFloor)
      {
        rlFloor tmp = (rlFloor)o;
        if ((tmp.x == to.x) && (tmp.y == to.y))
          done = true;
        else
        {
          x = nx;
          y = ny;
          step++;
        }
      }
      if (o instanceof rlWall)
      {
        rlWall tmp = (rlWall)o;
        if (tmp.kind == rlWall.Kind.ROCK)
        {
          map.get(ny).set(nx, new rlFloor(rlFloor.Kind.TUNN, nx, ny));
          x = nx;
          y = ny;
          step++;
        }
        else
          turn = true;
      }
      if ((step == 0) && (pass > 500))
      {
        done = true;
        res = false;
        System.out.println("stuck: " + o.getClass().getName());
      }
    }
    if (res)
      System.out.println("Tunnel digged, step: " + Integer.toString(step) + " pass: " + Integer.toString(pass));
    else
      System.out.println("Tunnel failed, step: " + Integer.toString(step) + " pass: " + Integer.toString(pass));
    return res;
  }

  private void buildGraf()
  {
    System.out.println("Building graf...");
    int x = 0, y = 0, step = 0, size = 0, last = 0;
    rlObj o;
    ArrayList<rlObj> toGraf = new ArrayList<rlObj>();
    if (graf.isEmpty())
      graf.add(doors.get(0));
    while (size != graf.size())
    {
      step++;
      if ((step % 10) == 0)
        System.out.println("...step: " + Integer.toString(step));
      size = graf.size();
      if ((size % 10) == 0)
        System.out.println("...size: " + Integer.toString(size));
      for (int i = last; i < size; i++)
      {
        o = graf.get(i);
        if (o instanceof rlDoor)
        {
          x = ((rlDoor)o).x;
          y = ((rlDoor)o).y;
        }
        if (o instanceof rlFloor)
        {
          x = ((rlFloor)o).x;
          y = ((rlFloor)o).y;
        }
        o = map.get(y - 1).get(x);
        if ((o instanceof rlDoor) || (o instanceof rlFloor))
          if (!graf.contains(o))
            if (!toGraf.contains(o))
              toGraf.add(o);
        o = map.get(y + 1).get(x);
        if ((o instanceof rlDoor) || (o instanceof rlFloor))
          if (!graf.contains(o))
            if (!toGraf.contains(o))
              toGraf.add(o);
        o = map.get(y).get(x - 1);
        if ((o instanceof rlDoor) || (o instanceof rlFloor))
          if (!graf.contains(o))
            if (!toGraf.contains(o))
              toGraf.add(o);
        o = map.get(y).get(x + 1);
        if ((o instanceof rlDoor) || (o instanceof rlFloor))
          if (!graf.contains(o))
            if (!toGraf.contains(o))
              toGraf.add(o);
      }
      last = size;
      graf.addAll(toGraf);
      toGraf.clear();
    }
    rlDoor d;
    rlObj sa, sb;
    boolean del;
    for (int i = graf.size() - 1; i > 0; i--)
    {
      o = graf.get(i);
      if (o instanceof rlDoor)
      {
        del = false;
        d = (rlDoor)o;
        if ((d.dir == rlDoor.Dir.N) || (d.dir == rlDoor.Dir.S))
        {
          sa = map.get(d.y - 1).get(d.x);
          sb = map.get(d.y + 1).get(d.x);
        }
        else
        {
          sa = map.get(d.y).get(d.x - 1);
          sb = map.get(d.y).get(d.x + 1);
        }
        if (sa instanceof rlWall)
          if (((rlWall)sa).kind == rlWall.Kind.ROCK)
            del = true;
        if (sb instanceof rlWall)
          if (((rlWall)sb).kind == rlWall.Kind.ROCK)
            del = true;
        if (del)
          graf.remove(o);
      }
    }
    System.out.println("Graf built, step: " + Integer.toString(step));
  }
}
