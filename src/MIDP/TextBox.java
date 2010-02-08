/*
 * Catcher
 *
 * License: GPL v3
 * Authors: richard_jonsson@hotmail.com, tommyc@lavabit.com
 */
package MIDP;

import javax.microedition.lcdui.Graphics;

public class TextBox {
    private int yScroll=0;
    private String s="";
    private boolean contentsBelowView=false;

    public void setText(String s) {
        yScroll = 0;
        this.s = s+" "; //Hack: add space to make last word linebreak if needed
    }

    public void scrollUp() {
        yScroll = (--yScroll<0? 0:yScroll);
    }

    public void scrollDown() {
        yScroll = (contentsBelowView? yScroll:++yScroll);
    }

    public void paint(Graphics g, int x, int y, int width, int height) {
        int spaceWidth=g.getFont().charWidth(' ');
        int lineHeight=g.getFont().getHeight();
        g.setClip(x, y, width, height);
        g.setColor(CatcherCanvas.COLOR_BACKGROUND);
        g.fillRect(x, y, width, height);
        g.setColor(CatcherCanvas.COLOR_TEXT);
        String word="";
        int wordWidth;
        int xPos=x;
        int yPos=y-yScroll*lineHeight;
        for (int i=0;i<s.length();i++) {
            switch (s.charAt(i)) {
                case '\n':
                case ' ':
                    wordWidth = g.getFont().stringWidth(word);
                    if (xPos+wordWidth > width) {
                        xPos = wordWidth+spaceWidth;
                        yPos += lineHeight;
                        g.drawString(word, x, yPos, Graphics.TOP|Graphics.LEFT);
                    } else {
                        g.drawString(word, xPos, yPos, Graphics.TOP|Graphics.LEFT);
                        xPos += wordWidth+spaceWidth;
                    }
                    word = "";
                    if (s.charAt(i) == '\n') {
                        xPos = x;
                        yPos += lineHeight;
                    }
                    break;
                default:
                    word += s.charAt(i);
            }
        }
        g.drawString(word, xPos, yPos, Graphics.TOP|Graphics.LEFT);
        contentsBelowView = (yPos+lineHeight < y+height? true:false);
    }
}
