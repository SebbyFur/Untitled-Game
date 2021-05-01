import untitledgame.terrain.*;

import java.awt.*;
import javax.swing.*;

public class FullMapViewer extends JFrame {
    public static void main(String[] args) {
        new FullMapViewer();
    }

    private Map map = new Map(4, 4, 5456464l);
    
    public FullMapViewer() {
        setSize(1200,900);
	    setLocationRelativeTo(null);
	    setTitle("Game save name");
	    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel mapView = new JPanel();
        mapView.setLayout(new GridLayout(map.getSizeX()*15, map.getSizeY()*15));

        Square[][] squares = new Square[map.getSizeX()*15][map.getSizeX()*15];

        for (int x = 0; x < squares.length; x++) {
            for (int y = 0; y < squares[x].length; y++) {
                squares[x][y] = new Square("assets/textures/terrain/Herbe3.png", 0, 0);
                mapView.add(squares[x][y]);
            }
        }

        for (int xChunk = 0; xChunk < map.getSizeX(); xChunk++) {
            for (int yChunk = 0; yChunk < map.getSizeY(); yChunk++) {
                for (int xSquare = 0; xSquare < 15; xSquare++) {
                    for (int ySquare = 0; ySquare < 15; ySquare++) {
                        squares[xSquare+xChunk*15][ySquare+yChunk*15].setTexture(map.getChunkAtPos(xChunk, yChunk).getContentAtPos(xSquare, ySquare).getTexture());
                    }
                }
            }
        }

        validate();
        repaint();

        getContentPane().add(mapView);

        setVisible(true);
    }
}