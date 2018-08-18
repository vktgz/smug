package smug;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class rlMap
{
  final private int MIN_ROOMS = 3;
  final private int MAX_ROOMS = 7;
  final private int MIN_ROOM_SIZE = 5;
  final private int MAX_ROOM_SIZE = 9;
  final private int MIN_DOORS = 1;
  final private int MAX_DOORS = 2;
  final private int MIN_FREE_DOORS = 1;
  final private int MAX_FREE_DOORS = 3;

  public ArrayList<ArrayList<rlObj>> map;
  private int cols;
  private int rows;
  private ArrayList<rlDoor> doors;
  public HashMap stairs;
  private ArrayList<rlObj> graf;
  public ArrayList<rlObj> timer;
  public String mID;
  public int mLvl;

  public rlMap(int ncols, int nrows, String nID, int nLvl)
  {
    cols = ncols;
    rows = nrows;
    mID = nID;
    mLvl = nLvl;
    timer = new ArrayList<rlObj>();
    doors = new ArrayList<rlDoor>();
    stairs = new HashMap();
    graf = new ArrayList<rlObj>();
    map = new ArrayList<ArrayList<rlObj>>(rows);
    cleanMap();
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
        {
          tmp.add(new rlWall(rlWall.Kind.SIDE));
        }
        else
        {
          tmp.add(new rlWall(rlWall.Kind.ROCK));
        }
      }
      map.add(r, tmp);
    }
  }

  public void generateRandomMap(rlDoor.Dir dv)
  {
    Random gen = new Random();
    boolean done = false;
    while (!done)
    {
      System.out.println("Generating map...");
      cleanMap();
      int rs = MIN_ROOMS + gen.nextInt(MAX_ROOMS - MIN_ROOMS + 1);
      for (int i = 0; i < rs; i++)
      {
        generateRoom();
      }
      rs = MIN_FREE_DOORS + gen.nextInt(MAX_FREE_DOORS - MIN_FREE_DOORS + 1);
      for (int i = 0; i < rs; i++)
      {
        generateFreeDoor();
      }
      done = generateTunnels();
    }
    generateStairs(dv);
    generateItems();
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
      {
        for (int cy = y; cy <= h; cy++)
        {
          if (((cx == x) || (cx == w)) && ((cy == y) || (cy == h)))
          {
            done = done & checkCorner(map.get(cy).get(cx));
          }
          if ((((cx == x) || (cx == w)) && (cy != y) && (cy != h)) || (((cy == y) || (cy == h)) && (cx != x) && (cx != w)))
          {
            done = done & checkWall(map.get(cy).get(cx));
          }
          if ((cx != x) && (cx != w) && (cy != y) && (cy != h))
          {
            done = done & checkFloor(map.get(cy).get(cx));
          }
        }
      }
      if (done)
      {
        rlObj o;
        rlWall ow;
        for (int cx = x; cx <= w; cx++)
        {
          for (int cy = y; cy <= h; cy++)
          {
            if (((cx == x) || (cx == w)) && ((cy == y) || (cy == h)))
            {
              ow = (rlWall)map.get(cy).get(cx);
              if ((ow.kind == rlWall.Kind.ROCK) || (ow.kind == rlWall.Kind.WALL))
              {
                ow.kind = rlWall.Kind.CORN;
              }
            }
            if ((((cx == x) || (cx == w)) && (cy != y) && (cy != h)) || (((cy == y) || (cy == h)) && (cx != x) && (cx != w)))
            {
              o = map.get(cy).get(cx);
              if (o instanceof rlWall)
              {
                ow = (rlWall)o;
                if (ow.kind == rlWall.Kind.ROCK)
                {
                  ow.kind = rlWall.Kind.WALL;
                }
              }
            }
            if ((cx != x) && (cx != w) && (cy != y) && (cy != h))
            {
              map.get(cy).set(cx, new rlFloor(rlFloor.Kind.ROOM, cx, cy));
            }
          }
        }
        int ds = MIN_DOORS + gen.nextInt(MAX_DOORS - MIN_DOORS + 1);
        for (int i = 0; i < ds; i++)
        {
          generateDoor(x, y, w, h);
        }
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
    {
      res = ((rlWall)w).kind == rlWall.Kind.ROCK;
    }
    return res;
  }

  private boolean checkDoor(rlObj o)
  {
    boolean res = o instanceof rlFloor;
    if (!res)
    {
      res = o instanceof rlWall;
      if (res)
      {
        res = ((rlWall)o).kind == rlWall.Kind.ROCK;
      }
    }
    return res;
  }

  private void checkTunnel(rlWall w, int wx, int wy, rlDoor.Dir d)
  {
    rlObj ol, or, ob, of;
    boolean dig = true;
    if ((d == rlDoor.Dir.N) || (d == rlDoor.Dir.S))
    {
      ol = map.get(wy).get(wx - 1);
      or = map.get(wy).get(wx + 1);
      ob = map.get(wy - 1).get(wx);
      of = map.get(wy + 1).get(wx);
    }
    else
    {
      ol = map.get(wy - 1).get(wx);
      or = map.get(wy + 1).get(wx);
      ob = map.get(wy).get(wx - 1);
      of = map.get(wy).get(wx + 1);
    }
    if (ol instanceof rlFloor)
    {
      dig = false;
    }
    if (or instanceof rlFloor)
    {
      dig = false;
    }
    if (ol instanceof rlDoor)
    {
      dig = true;
    }
    if (or instanceof rlDoor)
    {
      dig = true;
    }
    if (ob instanceof rlDoor)
    {
      dig = true;
    }
    if (of instanceof rlDoor)
    {
      dig = true;
    }
    if (!dig)
    {
      w.kind = rlWall.Kind.CORN;
    }
  }

  private boolean checkStair(int sx, int sy)
  {
    boolean res = true;
    rlObj o;
    for (int y = sy - 1; y < sy + 2; y++)
    {
      for (int x = sx - 1; x < sx + 2; x++)
      {
        o = map.get(y).get(x);
        res = res && (o instanceof rlFloor);
        if (res)
        {
          res = res && (((rlFloor)o).kind == rlFloor.Kind.ROOM);
        }
        if (!res)
        {
          break;
        }
      }
    }
    return res;
  }

  private void generateDoor(int rx, int ry, int rw, int rh)
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
          {
            if (checkDoor(map.get(y - 1).get(x)))
            {
              d = new rlDoor(rlDoor.Dir.N, rlDoor.Kind.ROOM, x, y);
            }
          }
          if (y == rh)
          {
            if (checkDoor(map.get(y + 1).get(x)))
            {
              d = new rlDoor(rlDoor.Dir.S, rlDoor.Kind.ROOM, x, y);
            }
          }
          if (x == rx)
          {
            if (checkDoor(map.get(y).get(x - 1)))
            {
              d = new rlDoor(rlDoor.Dir.W, rlDoor.Kind.ROOM, x, y);
            }
          }
          if (x == rw)
          {
            if (checkDoor(map.get(y).get(x + 1)))
            {
              d = new rlDoor(rlDoor.Dir.E, rlDoor.Kind.ROOM, x, y);
            }
          }
          if (d != null)
          {
            map.get(y).set(x, d);
            doors.add(d);
            if ((y == ry) || (y == rh))
            {
              w = (rlWall)map.get(y).get(x + 1);
              if (w.kind != rlWall.Kind.SIDE)
              {
                w.kind = rlWall.Kind.CORN;
              }
              w = (rlWall)map.get(y).get(x - 1);
              if (w.kind != rlWall.Kind.SIDE)
              {
                w.kind = rlWall.Kind.CORN;
              }
            }
            if ((x == rx) || (x == rw))
            {
              w = (rlWall)map.get(y + 1).get(x);
              if (w.kind != rlWall.Kind.SIDE)
              {
                w.kind = rlWall.Kind.CORN;
              }
              w = (rlWall)map.get(y - 1).get(x);
              if (w.kind != rlWall.Kind.SIDE)
              {
                w.kind = rlWall.Kind.CORN;
              }
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
    {
      for (int y = 0; y < rows; y++)
      {
        buf.put(x + 1, y + 1, map.get(y).get(x).getSymbol());
      }
    }
  }

  private boolean generateTunnels()
  {
    Random gen = new Random();
    ArrayList<rlDoor> rd = new ArrayList<rlDoor>();
    rlDoor md;
    while (!doors.isEmpty())
    {
      md = doors.get(gen.nextInt(doors.size()));
      doors.remove(md);
      rd.add(md);
    }
    doors.addAll(rd);
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
        {
          from = null;
        }
        else
        {
          break;
        }
      }
      done = (from == null);
      if (!done)
      {
        res = digTunnel(from);
        if (!res)
        {
          done = true;
        }
      }
      if (step > 100)
      {
        System.out.println("Loop break");
        res = false;
        done = true;
      }
    }
    return res;
  }

  private rlObj findTarget(int x, int y)
  {
    Random gen = new Random();
    rlObj to, tmp;
    int diff, ndiff;
    to = graf.get(0);
    diff = (Math.abs(x - to.x) + Math.abs(y - to.y));
    for (int i = 1; i < graf.size(); i++)
    {
      tmp = graf.get(i);
      if (tmp instanceof rlFloor)
      {
        if (((rlFloor)tmp).kind == rlFloor.Kind.ROOM)
        {
          continue;
        }
      }
      ndiff = (Math.abs(x - tmp.x) + Math.abs(y - tmp.y));
      if (ndiff == diff)
      {
        if (gen.nextBoolean())
        {
          to = tmp;
        }
      }
      if (ndiff < diff)
      {
        to = tmp;
        diff = ndiff;
      }
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
    {
      rlDoor tmp = (rlDoor)from;
      d = tmp.dir;
      if (tmp.kind == rlDoor.Kind.TUNN)
      {
        rlObj sb = null;
        if (d == rlDoor.Dir.N)
        {
          sb = map.get(tmp.y + 1).get(tmp.x);
        }
        else if (d == rlDoor.Dir.E)
        {
          sb = map.get(tmp.y).get(tmp.x - 1);
        }
        else if (d == rlDoor.Dir.S)
        {
          sb = map.get(tmp.y - 1).get(tmp.x);
        }
        else if (d == rlDoor.Dir.W)
        {
          sb = map.get(tmp.y).get(tmp.x + 1);
        }
        if (sb instanceof rlWall)
        {
          if (((rlWall)sb).kind == rlWall.Kind.ROCK)
          {
            if (d == rlDoor.Dir.N)
            {
              d = rlDoor.Dir.S;
            }
            else if (d == rlDoor.Dir.S)
            {
              d = rlDoor.Dir.N;
            }
            else if (d == rlDoor.Dir.W)
            {
              d = rlDoor.Dir.E;
            }
            else if (d == rlDoor.Dir.E)
            {
              d = rlDoor.Dir.W;
            }
          }
        }
      }
    }
    rlDoor.Dir nd = d;
    rlObj to, o;
    int nx = 0, ny = 0, dh, dw, tc = 0;
    boolean turn = false, swd = false;
    System.out.println("Digging tunnel...");
//    to = findTarget(x, y);
    while (!done)
    {
      pass++;
      if ((pass % 10) == 0)
      {
        System.out.println("...pass: " + Integer.toString(pass));
      }
      if ((step % 10) == 0)
      {
        System.out.println("...step: " + Integer.toString(step));
      }
      to = findTarget(x, y);
      if (step > 0)
      {
        if ((step > 1) && (!turn))
        {
          dh = Math.abs(to.x - x);
          dw = Math.abs(to.y - y);
          if (dw == 0)
          {
            if (to.x > x)
            {
              nd = rlDoor.Dir.E;
            }
            else
            {
              nd = rlDoor.Dir.W;
            }
          }
          else if (dh == 0)
          {
            if (to.y > y)
            {
              nd = rlDoor.Dir.S;
            }
            else
            {
              nd = rlDoor.Dir.N;
            }
          }
          else if ((dw < dh) && (!swd))
          {
            if (to.y > y)
            {
              nd = rlDoor.Dir.S;
            }
            else
            {
              nd = rlDoor.Dir.N;
            }
          }
          else if (to.x > x)
          {
            nd = rlDoor.Dir.E;
          }
          else
          {
            nd = rlDoor.Dir.W;
          }
          if (((d == rlDoor.Dir.N) && (nd == rlDoor.Dir.S)) || ((d == rlDoor.Dir.S) && (nd == rlDoor.Dir.N)))
          {
            if (to.x > x)
            {
              nd = rlDoor.Dir.E;
            }
            else
            {
              nd = rlDoor.Dir.W;
            }
          }
          if (((d == rlDoor.Dir.W) && (nd == rlDoor.Dir.E)) || ((d == rlDoor.Dir.E) && (nd == rlDoor.Dir.W)))
          {
            if (to.y > y)
            {
              nd = rlDoor.Dir.S;
            }
            else
            {
              nd = rlDoor.Dir.N;
            }
          }
//          if (((d == rlDoor.Dir.N) && (nd == rlDoor.Dir.S)) || ((d == rlDoor.Dir.S) && (nd == rlDoor.Dir.N)) || ((d == rlDoor.Dir.W) && (nd == rlDoor.Dir.E)) || ((d == rlDoor.Dir.E) && (nd == rlDoor.Dir.W)))
//            turn = true;
        }
        if (turn)
        {
          if (nd == rlDoor.Dir.N)
          {
            nd = rlDoor.Dir.E;
          }
          else if (nd == rlDoor.Dir.E)
          {
            nd = rlDoor.Dir.S;
          }
          else if (nd == rlDoor.Dir.S)
          {
            nd = rlDoor.Dir.W;
          }
          else if (nd == rlDoor.Dir.W)
          {
            nd = rlDoor.Dir.N;
          }
        }
        d = nd;
      }
      nx = x;
      ny = y;
      turn = false;
      if (d == rlDoor.Dir.N)
      {
        ny = y - 1;
      }
      if (d == rlDoor.Dir.S)
      {
        ny = y + 1;
      }
      if (d == rlDoor.Dir.W)
      {
        nx = x - 1;
      }
      if (d == rlDoor.Dir.E)
      {
        nx = x + 1;
      }
      o = map.get(ny).get(nx);
      if (o instanceof rlDoor)
      {
        rlDoor tmp = (rlDoor)o;
        if ((tmp.x == to.x) && (tmp.y == to.y))
        {
          done = true;
        }
        else
        {
          turn = true;
        }
      }
      if (o instanceof rlFloor)
      {
        rlFloor tmp = (rlFloor)o;
        if ((tmp.x == to.x) && (tmp.y == to.y))
        {
          done = true;
        }
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
          checkTunnel(tmp, nx, ny, d);
          if (tmp.kind == rlWall.Kind.ROCK)
          {
            map.get(ny).set(nx, new rlFloor(rlFloor.Kind.TUNN, nx, ny));
            x = nx;
            y = ny;
            step++;
          }
          else
          {
            turn = true;
          }
        }
        else
        {
          turn = true;
        }
      }
      if (turn)
      {
        tc++;
        if (tc > 2)
        {
          swd = !swd;
          tc = 0;
        }
      }
      else
      {
        turn = false;
      }
      if (!done)
      {
        o = map.get(y).get(x + 1);
        if (graf.contains(o))
        {
          done = true;
        }
        o = map.get(y).get(x - 1);
        if (graf.contains(o))
        {
          done = true;
        }
        o = map.get(y + 1).get(x);
        if (graf.contains(o))
        {
          done = true;
        }
        o = map.get(y - 1).get(x);
        if (graf.contains(o))
        {
          done = true;
        }
      }
      if (step > 1000)
      {
        done = true;
        res = false;
      }
      if (pass > 5000)
      {
        done = true;
        res = false;
      }
      if ((step == 0) && (pass > 50))
      {
        done = true;
        res = false;
        System.out.println("stuck: " + o.getClass().getName());
      }
    }
    if (res)
    {
      System.out.println("Tunnel digged, step: " + Integer.toString(step) + " pass: " + Integer.toString(pass));
    }
    else
    {
      System.out.println("Tunnel failed, step: " + Integer.toString(step) + " pass: " + Integer.toString(pass));
    }
    /*    for (int dy = 0; dy < rows; dy++)
		for (int dx = 0; dx < cols; dx++)
		map.get(dy).get(dx).getSymbol().bgColor = rlColor.BLACK;
		map.get(ny).get(nx).getSymbol().bgColor = rlColor.LBLUE;
		map.get(y).get(x).getSymbol().bgColor = rlColor.BLUE;
		map.get(from.y).get(from.x).getSymbol().bgColor = rlColor.YELLOW;
		map.get(to.y).get(to.x).getSymbol().bgColor = rlColor.LGREEN; */
    return res;
  }

  private void buildGraf()
  {
    System.out.println("Building graf...");
    int x = 0, y = 0, step = 0, size = 0, last = 0;
    rlObj o;
    ArrayList<rlObj> toGraf = new ArrayList<rlObj>();
    if (graf.isEmpty())
    {
      for (int i = 0; i < doors.size(); i++)
      {
        if (doors.get(i).kind == rlDoor.Kind.ROOM)
        {
          graf.add(doors.get(i));
          break;
        }
      }
    }
    while (size != graf.size())
    {
      step++;
      if ((step % 10) == 0)
      {
        System.out.println("...step: " + Integer.toString(step));
      }
      size = graf.size();
      if ((size % 10) == 0)
      {
        System.out.println("...size: " + Integer.toString(size));
      }
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
        {
          if (!graf.contains(o))
          {
            if (!toGraf.contains(o))
            {
              toGraf.add(o);
            }
          }
        }
        o = map.get(y + 1).get(x);
        if ((o instanceof rlDoor) || (o instanceof rlFloor))
        {
          if (!graf.contains(o))
          {
            if (!toGraf.contains(o))
            {
              toGraf.add(o);
            }
          }
        }
        o = map.get(y).get(x - 1);
        if ((o instanceof rlDoor) || (o instanceof rlFloor))
        {
          if (!graf.contains(o))
          {
            if (!toGraf.contains(o))
            {
              toGraf.add(o);
            }
          }
        }
        o = map.get(y).get(x + 1);
        if ((o instanceof rlDoor) || (o instanceof rlFloor))
        {
          if (!graf.contains(o))
          {
            if (!toGraf.contains(o))
            {
              toGraf.add(o);
            }
          }
        }
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
        {
          if (((rlWall)sa).kind == rlWall.Kind.ROCK)
          {
            del = true;
          }
        }
        if (sb instanceof rlWall)
        {
          if (((rlWall)sb).kind == rlWall.Kind.ROCK)
          {
            del = true;
          }
        }
        if (del)
        {
          graf.remove(o);
        }
      }
    }
    System.out.println("Graf built, step: " + Integer.toString(step));
  }

  private void generateFreeDoor()
  {
    System.out.println("Generating free door...");
    Random gen = new Random();
    boolean done = false;
    while (!done)
    {
      int x = 3 + gen.nextInt(cols - 6);
      int y = 3 + gen.nextInt(rows - 6);
      done = true;
      for (int cy = y - 1; cy < y + 2; cy++)
      {
        for (int cx = x - 1; cx < x + 2; cx++)
        {
          rlObj o = map.get(cy).get(cx);
          if (o instanceof rlWall)
          {
            if (((rlWall)o).kind == rlWall.Kind.ROCK)
            {
              continue;
            }
          }
          done = false;
        }
      }
      if (done)
      {
        rlDoor.Dir nd = rlDoor.Dir.N;
        int ds = gen.nextInt(4);
        if (ds == 1)
        {
          nd = rlDoor.Dir.E;
        }
        if (ds == 2)
        {
          nd = rlDoor.Dir.S;
        }
        if (ds == 3)
        {
          nd = rlDoor.Dir.W;
        }
        rlDoor d = new rlDoor(nd, rlDoor.Kind.TUNN, x, y);
        map.get(y).set(x, d);
        doors.add(d);
        if ((nd == rlDoor.Dir.N) || (nd == rlDoor.Dir.S))
        {
          ((rlWall)map.get(y).get(x + 1)).kind = rlWall.Kind.CORN;
          ((rlWall)map.get(y).get(x - 1)).kind = rlWall.Kind.CORN;
          ((rlWall)map.get(y - 1).get(x + 1)).kind = rlWall.Kind.CORN;
          ((rlWall)map.get(y - 1).get(x - 1)).kind = rlWall.Kind.CORN;
          ((rlWall)map.get(y + 1).get(x + 1)).kind = rlWall.Kind.CORN;
          ((rlWall)map.get(y + 1).get(x - 1)).kind = rlWall.Kind.CORN;
        }
        else
        {
          ((rlWall)map.get(y + 1).get(x)).kind = rlWall.Kind.CORN;
          ((rlWall)map.get(y - 1).get(x)).kind = rlWall.Kind.CORN;
          ((rlWall)map.get(y + 1).get(x - 1)).kind = rlWall.Kind.CORN;
          ((rlWall)map.get(y - 1).get(x - 1)).kind = rlWall.Kind.CORN;
          ((rlWall)map.get(y + 1).get(x + 1)).kind = rlWall.Kind.CORN;
          ((rlWall)map.get(y - 1).get(x + 1)).kind = rlWall.Kind.CORN;
        }
      }
    }
  }

  private void generateStairs(rlDoor.Dir dv)
  {
    Random gen = new Random();
    int x = 0, y = 0, lx = 0, dl = 0;
    boolean done = false;
    while (!done)
    {
      x = 2 + gen.nextInt(cols - 4);
      y = 2 + gen.nextInt(rows - 4);
      done = checkStair(x, y);
      if (done)
      {
        rlDoor s = new rlDoor(rlDoor.Dir.U, rlDoor.Kind.PASS, x, y);
        if (dv == rlDoor.Dir.D)
        {
          dl = -1;
        }
        else
        {
          dl = 1;
        }
        s.setID(mID, mLvl + dl);
        map.get(y).set(x, s);
        stairs.put(s.getID(), s);
      }
    }
    lx = x;
    done = false;
    while (!done)
    {
      x = 2 + gen.nextInt(cols - 4);
      y = 2 + gen.nextInt(rows - 4);
      done = checkStair(x, y);
      if (done)
      {
        done = Math.abs(lx - x) > MAX_ROOM_SIZE;
        if (done)
        {
          rlDoor s = new rlDoor(rlDoor.Dir.D, rlDoor.Kind.PASS, x, y);
          if (dv == rlDoor.Dir.D)
          {
            dl = 1;
          }
          else
          {
            dl = -1;
          }
          s.setID(mID, mLvl + dl);
          map.get(y).set(x, s);
          stairs.put(s.getID(), s);
        }
      }
    }
  }

  public void setVisible(int cx, int cy, int r)
  {
    rlCircle c = rlCircle.circle(r);
    rlLine ln;
    rlPoint pt;
    rlObj o;
    int x, y;
    for (x = 0; x < cols; x++)
    {
      for (y = 0; y < rows; y++)
      {
        map.get(y).get(x).setVisible(true, false);
      }
    }
    for (int l = 0; l < c.lines(); l++)
    {
      ln = c.getLine(l);
      for (int i = 0; i < ln.length(); i++)
      {
        pt = ln.get(i);
        x = pt.col + cx;
        y = pt.row + cy;
        if ((x >= 0) && (x < cols) && (y >= 0) && (y < rows))
        {
          o = map.get(y).get(x);
          o.setVisible(false, true);
          o.setVisible(true, true);
          if (o instanceof rlWall)
          {
            break;
          }
          if (o instanceof rlDoor)
          {
            rlDoor d = (rlDoor)o;
            if (d.kind != rlDoor.Kind.PASS)
            {
              if (!d.open)
              {
                break;
              }
            }
          }
        }
      }
    }
  }

  public String getID()
  {
    return mID + Integer.toString(mLvl);
  }

  void generateSpecialMap(String gID)
  {
    if (gID.equals("CTY"))
    {
      for (int r = 1; r < rows - 1; r++)
      {
        for (int c = 1; c < cols - 1; c++)
        {
          if (((((r == 5) || (r == 9)) && (c > 4) && (c < 10)) || (((c == 5) || (c == 9)) && (r > 4) && (r < 10))) || ((((r == 5) || (r == 9)) && (c > 21) && (c < 27)) || (((c == 22) || (c == 26)) && (r > 4) && (r < 10))))
          {
            map.get(r).set(c, new rlWall(rlWall.Kind.ROCK));
          }
          else
          {
            map.get(r).set(c, new rlFloor(rlFloor.Kind.ROOM, c, r));
          }
        }
      }
      map.get(7).set(9, new rlDoor(rlDoor.Dir.E, rlDoor.Kind.ROOM, 9, 7));
      map.get(7).set(22, new rlDoor(rlDoor.Dir.W, rlDoor.Kind.ROOM, 22, 7));
      for (int c = 14; c < 19; c++)
      {
        map.get(9).set(c, new rlWall(rlWall.Kind.ROCK));
        map.get(13).set(c, new rlWall(rlWall.Kind.ROCK));
      }
      map.get(12).set(18, new rlWall(rlWall.Kind.ROCK));
      map.get(12).set(14, new rlWall(rlWall.Kind.ROCK));
      map.get(11).set(19, new rlWall(rlWall.Kind.ROCK));
      map.get(11).set(18, new rlWall(rlWall.Kind.ROCK));
      map.get(11).set(17, new rlWall(rlWall.Kind.ROCK));
      map.get(11).set(16, new rlWall(rlWall.Kind.ROCK));
      map.get(11).set(14, new rlWall(rlWall.Kind.ROCK));
      map.get(11).set(13, new rlWall(rlWall.Kind.ROCK));
      map.get(10).set(19, new rlWall(rlWall.Kind.ROCK));
      map.get(10).set(13, new rlWall(rlWall.Kind.ROCK));
      map.get(9).set(19, new rlWall(rlWall.Kind.ROCK));
      map.get(9).set(13, new rlWall(rlWall.Kind.ROCK));
      map.get(9).set(18, new rlDoor(rlDoor.Dir.N, rlDoor.Kind.ROOM, 18, 9));
      rlDoor st;
      st = new rlDoor(rlDoor.Dir.D, rlDoor.Kind.PASS, 7, 7);
      st.setID("INF", 1);
      map.get(7).set(7, st);
      stairs.put(st.getID(), st);
      st = new rlDoor(rlDoor.Dir.D, rlDoor.Kind.PASS, 24, 7);
      st.setID("RDM", 1);
      map.get(7).set(24, st);
      stairs.put(st.getID(), st);
      st = new rlDoor(rlDoor.Dir.U, rlDoor.Kind.PASS, 17, 12);
      st.setID("TWR", 1);
      map.get(12).set(17, st);
      stairs.put(st.getID(), st);
    }
  }

  private void generateItems()
  {
    Random gen = new Random();
    if (gen.nextFloat() < 0.333)
    {
      generateItem(rlItem.Kind.GOLD);
    }
    if (gen.nextFloat() < 0.25)
    {
      generateItem(rlItem.Kind.GOLD);
    }
    if (gen.nextFloat() < 0.1)
    {
      generateItem(rlItem.Kind.GOLD);
    }
  }

  private void generateItem(rlItem.Kind knd)
  {
    Random gen = new Random();
    if (knd == rlItem.Kind.GOLD)
    {
      boolean done = false;
      rlObj o = null;
      while (!done)
      {
        o = graf.get(gen.nextInt(graf.size()));
        if (o instanceof rlFloor)
        {
          if (((rlFloor)o).kind == rlFloor.Kind.ROOM)
          {
            if (o.items.isEmpty())
            {
              done = true;
            }
          }
        }
      }
      int cnt = mLvl + gen.nextInt((10 * mLvl) - mLvl + 1);
      rlItem gold = rlItem.makeGold(cnt, o.x, o.y);
      o.items.add(gold);
    }
  }
}
