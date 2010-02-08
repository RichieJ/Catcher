/*
 * Catcher
 *
 * License: GPL v3
 * Authors: richard_jonsson@hotmail.com, tommyc@lavabit.com
 */
package MIDP;

import GUI.ICacheView;
import GUI.IViewNavigator;
import System.Cache;
import System.Position;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;

public class CacheView extends CatcherCanvas implements ICacheView {

    private Cache cache;

    // Careful when changing globalItems! menuAction() assumes a certain order!
    private String[] viewItems = {"Cache list", "Set nearest", "Show hint"};
    private int page=0;
    private static final int PAGE_DESC=0;
    private static final int PAGE_LOGS=1;
    private TextBox textBox = new TextBox();

    public CacheView(IViewNavigator viewNavigator, ViewResources viewResources) {
        super(viewNavigator);

        setFullScreenMode(true);
        this.viewResources = viewResources;
        initFakeCache(); // Placeholder
        textBox.setText(cache.description);
    }

    // This is just a placeholder
    private void initFakeCache() {
        cache = new Cache();
        cache.name="Catcher fake cache placeholder";
        cache.code="OC3G4F5";
        cache.difficulty=0;
        cache.terrain=0;
        cache.type=cache.CT_REGULAR;
        cache.position= new Position(57.1, 14.8);
        cache.description="This is the description of the cache.";
        cache.hint="Under sten";
        cache.lastLogs="TFTC\n";
        int[] type={0,1};
        cache.lastLogsType=type;
    }

    public Cache getCache() {
        return cache;
    }

    public void setCache(Cache cache) {
        this.cache = cache;
    }

    private int getHeading(Position p) {
        return 0;
    }

    private int paintCacheAttributes(Graphics g) {
        int ht = sysFont.getHeight();
        int x = 0;
        int y = 20;
        int width = getWidth();
        int tl = Graphics.TOP|Graphics.LEFT;
        int height = (16>sysFont.getHeight()? 16 : sysFont.getHeight());
        paintCache(g, x, y, width, height, getHeading(cache.position), cache);
        y += sysFont.getHeight();
        return y;
    }

    public void paintView(Graphics g) {
        g.setColor(COLOR_BACKGROUND);
        g.fillRect(0, 0, getWidth(), getHeight()-HEIGHT_STATUSBAR);
        int y = paintCacheAttributes(g);
        textBox.paint(g, 0, y, getWidth(), getHeight()-y);
    }

    private void nextPage() {
        ++page;
        switch(page) {
            case 1:
                textBox.setText(cache.lastLogs);
                break;
            default:
                page=0;
                textBox.setText(cache.description);
        }
    }
    
    /**
     * Called when a menu item has been selected
     * @param menuItem: index in view menu array
     */
    void menuActionView(int menuItem) {
        switch(menuItem) {
            case 0:
                // Cache list
                System.out.println("menu Cache list (not impl)");
                break;
            case 1:
                // Set nearest
                System.out.println("menu Set nearest (not impl)");
                break;
            case 2:
                // Show hint
                System.out.println("menu Show hint (not impl)");
                break;
        }
    }

    /**
     * Called when a key is pressed.
     */
    protected  void keyPressedView(int keyCode) {
        switch(getGameAction(keyCode)) {
            case DOWN:
                textBox.scrollDown();
                repaint();
                break;
            case UP:
                textBox.scrollUp();
                repaint();
                break;
            case FIRE:
                nextPage();
                repaint();
                break;
        }
    }

    public void activate() {
        menu.viewItems(viewItems);
    }

    public void deactivate() {
        menu.clearViewItems();
    }
}