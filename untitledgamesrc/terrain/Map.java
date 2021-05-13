package terrain;

import objets.*;
import personnages.*;
/**
 * @author DELVIGNE Brian, DIOT Sébastien, GNALY-NGUYEN Kouadjo, LEHMAN Ylon
 * @version 10/05/2021
 */
public class Map implements java.io.Serializable {
    /**
     * Un tableau de tableaux de Chunk pour chaque case de la carte
     */
    private Chunk[][] map;
    /**
     * Un int pour connaitre la taille de la carte en longueur
     */
    private int sizeX;
    /**
     * Un int pour connaitre la taille de la carte en largeur
     */
    private int sizeY;
    /**
     * Un long pour la valeur de la "seed" c'est-a-dire uen carte unique avec un seul code
     */
    private long seed;
    /**
     * Un int pour savoir exactement dans quel Chunk se trouve le joueur sur l'axe X
     */
    public int curChunkX = 0;
    /**
     * Un int pour savoir exactement dans quel Chunk se trouve le joueur sur l'axe Y
     */
    public int curChunkY = 0;
    /**
     * Constructeur par initialisation
     * @param sizeX int pour la taille en longueur
     * @param sizeY int pour la taille en largeur
     * @param seed long pour la version de la carte
     */
    public Map(int sizeX, int sizeY, long seed) {
        map = new Chunk[sizeX][sizeY];
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.seed = seed;
        populateMap(seed);
    }
    /**
     * Constructeur par defaut
     */
    public Map() {
        this(4, 4, 99999l);
    }
    /**
     * Methode pour generer la carte
     * @param seed long pour generer une version de la carte
     */
    private void populateMap(long seed) {
        for (int x = 0; x < sizeX; x++) {
            for (int y = 0; y < sizeY; y++) {
                Chunk chunk = new Chunk(x, y);
                chunk.addFeatures(SquareType.TREE, 6);
                chunk.addFeatures(SquareType.ROCK, 2);
                chunk.addFeatures(SquareType.BUSHES, 3);
                chunk.perlinize(SquareType.GRASS1, seed * 14894465l, 18);
                chunk.perlinize(SquareType.GRASS2, seed * 132467885l, 18);
                chunk.reversePerlinize(SquareType.WATER2, seed * 4564646l, 10);
                map[x][y] = chunk;
            }
        }
    }
    /**
     * Getter pour la position d'un Chunk dans la carte grace a ses coordonnees
     * @param x int pour la position en X
     * @param y int pour la position en Y
     * @return Chunk 
     */
    public Chunk getChunkAtPos(int x, int y) {
        return map[x][y];
    }
    /**
     * Getter pour connaitre le Chunk dans lequel le joueur est actuellement
     * @return Chunk
     */
    public Chunk getCurrentlyLoadedChunk() {
        return map[curChunkX][curChunkY];
    }
    /**
     * Methode 
     * @return double
     */
    public double map(double x, double input_min, double input_max, double output_min, double output_max) {
        return (x - input_min)*(output_max-output_min)/(input_max-input_min)+output_min;
    }
    /**
     * Getter pour la taille de la carte en longueur
     * @return int
     */
    public int getSizeX() {
        return sizeX;
    }
    /**
     * Getter pour la taille de la carte en largeur
     * @return int
     */
    public int getSizeY() {
        return sizeY;
    }
    /**
     * Getter pour le seed de la map
     * @return long
     */
    public long getSeed() {
        return seed;
    }
    /**
     * Methode pour ajouter un personnage a une position donnee dans un chunk donne
     * @param mob APersonnage a placer
     * @param chunk Chunk dans lequel le personnage se deplacera
     * @param xSquare int les coordonnees en X ou le personnage sera deplace
     * @param ySquare int les coordonnees en Y ou le personnage sera deplace
     */
    public void addMobAtPos(APersonnage mob, Chunk chunk, int xSquare, int ySquare) {
        try {
            if (chunk == null ^ xSquare < 0 ^ ySquare < 0 ^ xSquare > 14 ^ ySquare > 14) {
                throw new IllegalArgumentException("Chunk is null, or square position is invalid. (must be [0;14]");
            }
            mob.squarePosX = chunk.getChunkPosX()*15+xSquare;
            mob.squarePosY = chunk.getChunkPosY()*15+ySquare;
            map[chunk.getChunkPosX()][chunk.getChunkPosY()].setMobAtPos(mob, xSquare%15, ySquare%15);
        } catch(IllegalArgumentException err) {
            err.printStackTrace();
        }
    }

    public int[] findValidSpawn(Chunk chunk) {
        int xValid = -1;
        int yValid = -1;
        boolean stop = false;

        while (!(stop)) {
            yValid++;
            while (!(stop)) {
                xValid++;
                if (chunk.getContentAtPos(xValid, yValid).isSpawnValid()) {
                    stop = true;
                }
            }
        }
        int[] pos = {xValid, yValid};

        return(pos);
    }

    public void spawnMob(APersonnage mob) {
        if (mob.squarePosX == -1 && mob.squarePosY == -1) {    
            Chunk chunk = getCurrentlyLoadedChunk();
            int[] pos = findValidSpawn(chunk);
            addMobAtPos(mob, chunk, pos[0]%15, pos[1]%15);
        } else {
            addMobAtPos(mob, getChunkOfMob(mob), mob.squarePosX%15, mob.squarePosY%15);
        }
    }

    /**
     * Methode pour la prochaine position du personnage
     * @param mob Le personnage a deplacer
     * @param direction La direction dans laquelle le personnage se dirige
     */
    public void changeMobPos(AHero mob, Direction direction) {
        int xNextPosition = mob.squarePosX+direction.x;
        int yNextPosition = mob.squarePosY+direction.y;

        if (xNextPosition >= 0 && xNextPosition < sizeX*15 && yNextPosition >= 0 && yNextPosition < sizeY*15) {
            Square squareNext = map[(int)(xNextPosition/15)][(int)(yNextPosition/15)].getContentAtPos(xNextPosition%15, yNextPosition%15);
            squareUpdate(mob, squareNext);
            if (!(map[(int)(xNextPosition/15)][(int)(yNextPosition/15)].getContentAtPos(xNextPosition%15, yNextPosition%15).getSquareType().hasBoundingBox) && (map[(int)(xNextPosition/15)][(int)(yNextPosition/15)].getContentAtPos(xNextPosition%15, yNextPosition%15).getMob() == null)) {
                map[(int)(mob.squarePosX/15)][(int)(mob.squarePosY/15)].removeMobAtPos(mob.squarePosX%15, mob.squarePosY%15);
                if ((int)(xNextPosition/15) != (int)(mob.squarePosX/15) ^ (int)(yNextPosition/15) != (int)(mob.squarePosY/15)) {
                    curChunkX += direction.x;
                    curChunkY += direction.y;
                }
                mob.squarePosX = xNextPosition;
                mob.squarePosY = yNextPosition;
                map[(int)(mob.squarePosX/15)][(int)(mob.squarePosY/15)].setMobAtPos(mob, mob.squarePosX%15, mob.squarePosY%15);
            }
        }
    }
    /**
     * Methode pour mettre a jour les blocs
     * @param mob AHero
     * @param squareNext Square
     */
    public void squareUpdate(AHero mob, Square squareNext) {
        if (squareNext.getSquareType() == SquareType.WATER2 && mob.getInventaire().retirerObjet(new Buche(2))) {
            squareNext.setSquareType(SquareType.WOOD);
        }

        if (squareNext.getSquareType() == SquareType.TREE) {
            squareNext.setSquareType(SquareType.SOUCHE);
            int random = (int)(Math.random()*(5-2)+2);
            mob.getInventaire().addObjetToInv(new Buche(random));
        }
    }

    public Chunk getChunkOfMob(APersonnage mob) {
        return map[(int)(mob.squarePosX/15)][(int)(mob.squarePosY/15)];
    }
}