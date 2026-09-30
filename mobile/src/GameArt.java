import javax.microedition.lcdui.Graphics;

/** Small, high-contrast sprites drawn at the actual tile size. */
public final class GameArt {
    private GameArt() { }

    private static void box(Graphics g, int x, int y, int s, int xx, int yy, int w, int h, int color) {
        g.setColor(color);
        g.fillRect(x + xx * s / 20, y + yy * s / 20, Math.max(1, w * s / 20), Math.max(1, h * s / 20));
    }

    public static void cat(Graphics g, int x, int y, int size) {
        box(g,x,y,size,4,15,13,3,0x513726);
        box(g,x,y,size,4,7,12,10,0xf4ae43);
        box(g,x,y,size,4,3,4,5,0xf4ae43);
        box(g,x,y,size,12,3,4,5,0xf4ae43);
        box(g,x,y,size,5,4,2,3,0xfbd4a2);
        box(g,x,y,size,13,4,2,3,0xfbd4a2);
        box(g,x,y,size,6,9,2,3,0x202a34);
        box(g,x,y,size,12,9,2,3,0x202a34);
        box(g,x,y,size,9,13,2,2,0x9b493e);
        box(g,x,y,size,2,16,5,2,0xf4ae43);
        box(g,x,y,size,17,11,2,6,0xf4ae43);
    }

    public static void person(Graphics g, int x, int y, int size, boolean enemy, int step) {
        box(g,x,y,size,4,17,12,2,0x49392e);
        box(g,x,y,size,5,9,10,7,enemy ? 0x985584 : 0xe3473f);
        box(g,x,y,size,6,3,8,7,0xf0c399);
        box(g,x,y,size,5,2,10,3,enemy ? 0xc9c5d0 : 0x513929);
        if (enemy) box(g,x,y,size,8,0,5,3,0xe1d7e2);
        box(g,x,y,size,7,6,2,1,0x232632);
        box(g,x,y,size,12,6,2,1,0x232632);
        box(g,x,y,size,3,10,2,5,0xf0c399);
        box(g,x,y,size,15,10,2,5,0xf0c399);
        box(g,x,y,size,6,16,3,2 + step,0x293442);
        box(g,x,y,size,12,16,3,3 - step,0x293442);
    }

    public static void basket(Graphics g, int x, int y, int size) {
        box(g,x,y,size,2,7,16,11,0x704730);
        box(g,x,y,size,3,8,14,9,0xcf9d63);
        for (int i = 5; i < 17; i += 4) box(g,x,y,size,i,8,1,9,0x946238);
        box(g,x,y,size,1,5,18,3,0xefc68f);
        box(g,x,y,size,3,12,14,1,0x946238);
    }

    public static void key(Graphics g, int x, int y, int size) {
        box(g,x,y,size,3,4,7,7,0xffdb65);
        box(g,x,y,size,5,6,3,3,0x856641);
        box(g,x,y,size,9,7,8,3,0xffdb65);
        box(g,x,y,size,13,10,2,3,0xffdb65);
        box(g,x,y,size,16,10,2,3,0xffdb65);
    }

    public static void stairs(Graphics g, int x, int y, int size, boolean up) {
        g.setColor(up ? 0x46706e : 0x515f87);
        g.fillRect(x + 1, y + 1, size - 2, size - 2);
        for (int i = 0; i < 4; i++) {
            g.setColor(0xb5cec7);
            int offset = (i + 1) * size / 5;
            g.drawLine(x + 3, y + offset, x + size - 4, y + offset);
        }
        g.setColor(0xffffff);
        int mid = x + size / 2, top = y + (up ? size / 4 : size * 3 / 4);
        g.drawLine(mid, y + size / 4, mid, y + size * 3 / 4);
        g.drawLine(mid, top, mid - size / 5, top + (up ? size / 5 : -size / 5));
        g.drawLine(mid, top, mid + size / 5, top + (up ? size / 5 : -size / 5));
    }

    public static void furniture(Graphics g, int x, int y, int width, int height, int type) {
        g.setColor(0x5c4334); g.fillRect(x + 2, y + 3, width - 2, height - 2);
        if (type == LevelData.BED) {
            g.setColor(0x663f31); g.fillRect(x + 1, y + 1, width - 3, height - 3);
            g.setColor(0xb5c5bd); g.fillRect(x + 4, y + 3, width - 9, height - 7);
            g.setColor(0x537e91); g.fillRect(x + 4, y + height / 3, width - 9, height * 2 / 3 - 4);
            g.setColor(0xf2e3c7); g.fillRect(x + 5, y + 4, width - 11, Math.max(2, height / 4));
        } else if (type == LevelData.SOFA) {
            g.setColor(0x32585a); g.fillRect(x + 1, y + 1, width - 3, height - 3);
            g.setColor(0x67908a); g.fillRect(x + 4, y + 4, width - 9, height - 9);
            g.setColor(0x365f60); g.drawLine(x + width / 2, y + 3, x + width / 2, y + height - 5);
        } else if (type == LevelData.TABLE) {
            g.setColor(0x56372d); g.fillRect(x + 2, y + 2, width - 5, height - 5);
            g.setColor(0xbc8b54); g.fillRect(x + 3, y + 2, width - 7, height - 8);
            g.setColor(0xe4c48b); g.drawRect(x + 5, y + 4, width - 12, height - 13);
        } else if (type == LevelData.DRAWER) {
            g.setColor(0x8a593e); g.fillRect(x + 1, y + 1, width - 3, height - 3);
            for (int yy = y + 4; yy < y + height - 3; yy += Math.max(5, height / 3)) {
                g.setColor(0x5b392d); g.drawLine(x + 2, yy, x + width - 4, yy);
                g.setColor(0xe2b969); g.fillRect(x + width / 2, yy + 2, 2, 2);
            }
        } else {
            g.setColor(0xb9764b); g.fillRect(x + width / 3, y + height / 2, width / 3, height / 2 - 1);
            g.setColor(0x4c8350); g.fillArc(x + 1, y + 1, width - 3, height * 2 / 3, 0, 360);
            g.setColor(0x80ab60); g.drawLine(x + width / 2, y + 2, x + width / 2, y + height * 2 / 3);
        }
    }
}
