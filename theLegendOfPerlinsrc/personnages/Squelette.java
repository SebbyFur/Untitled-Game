package personnages;
/**
 * @author DELVIGNE Brian, DIOT Sébastien, GNALY-NGUYEN Kouadjo, LEHMAN Ylon
 * @version 16/05/2021
 */
public class Squelette extends AVilain {
    /**
     * Constructeur par defaut
     * @param niveau int
     * @param squarePosX int 
     * @param squarePosY int
     */
    public Squelette(int niveau, int squarePosX, int squarePosY) {
        super(niveau, squarePosX, squarePosY, MobType.SKELETON);
        initStats();
    }
}
