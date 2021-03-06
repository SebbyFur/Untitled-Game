package objets;
/**
 * @author DELVIGNE Brian, DIOT Sébastien, GNALY-NGUYEN Kouadjo, LEHMAN Ylon
 * @version 16/05/2021
 */
public class Poulet extends AObjet {
	/**
	 * Constructeur par defaut
	 */
	public Poulet() {
		this(1);
	}
	/**
	 * Constructeur par initialisation
	 * @param count int
	 */
	public Poulet(int count) {
		super(100, 0, count, ObjetType.POULET, 0, 0);
	}
}