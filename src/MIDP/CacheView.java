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
import System.GPX;
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

        // temp debug code start

        // fake gpx waypoint
        String s = "</wpt>\n"+
"\n"+
"	<wpt lat=\"56.12038\" lon=\"12.14503\">\n"+
"	<time>2003-11-29T00:00:00Z</time>\n"+
"	<name>OS0000</name>\n"+
"	<desc>Hej hopp by joser, Virtual Cache (1.0/1.0)</desc>\n"+
"	<src>www.opencaching.se</src>\n"+
"	<url>http://www.opencaching.se/viewcache.php?cacheid=0</url>\n"+
"	<urlname>Hej hopp</urlname>\n"+
"	<sym>Geocache</sym>\n"+
"	<type>Geocache|Virtual Cache</type>\n"+
"	<geocache status=\"Available\" xmlns=\"http://geocaching.com.au/geocache/1\">\n"+
"			<name>Hej hopp</name>\n"+
"			<owner>joser</owner>\n"+
"			<locale></locale>\n"+
"			<state></state>\n"+
"			<country>United Kingdom</country>\n"+
"			<type>Virtual</type>\n"+
"			<container>Virtual</container>\n"+
"			<difficulty>1.0</difficulty>\n"+
"			<terrain>1.0</terrain>\n"+
"			<summary html=\"false\">Kort beskrivning.ÅäÖ</summary>\n"+
"			<description html=\"true\">Denna i&amp;ouml;gonfallande cachen finns inte.\n"+
"&lt;br&gt;</description>\n"+
"			\n"+
"			<licence></licence>\n"+
"			<logs>\n"+
"				\n"+
"<log id=\"80\">\n"+
"	<time>2009-12-30T00:00:00Z</time>\n"+
"	<geocacher>joser</geocacher>\n"+
"	<type>Note</type>\n"+
"	<text>&amp;nbsp;F&amp;ouml;lj med\n"+
"</text>\n"+
"</log>\n"+
"\n"+
"\n"+
"<log id=\"79\">\n"+
"	<time>2009-12-30T00:00:00Z</time>\n"+
"	<geocacher>banan</geocacher>\n"+
"	<type>Found</type>\n"+
"	<text>30.12.2009\n"+
"09:08:08\n"+
"F&amp;ouml;rsta loggen.&amp;nbsp;\n"+
"</text>\n"+
"</log>\n"+
"\n"+
"\n"+
"			</logs>\n"+
"		</geocache>\n"+
"	</wpt>\n"+
"\n";
        GPX.parse(s);
        // temp debug code end

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
        page = 0;
        textBox.setText(cache.description);
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