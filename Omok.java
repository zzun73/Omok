package test;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import javax.swing.*;

/**
 * <code>오목 게임</code> - 일반룰(오목룰)을 이용하는 오목판 및 게임.
 * https://namu.wiki/w/%EC%98%A4%EB%AA%A9(%EA%B2%8C%EC%9E%84)
 *
 * 사용법: 커맨드라인에서 java Omok [<판 크기>]
 * @author 꿀쥐
 * @version 1.0
 */
public class Omok
{
    /**
     * <code>메인</code> - 오목판을 초기화
     * 판 크기는 기본적으로 15지만 커맨드라인에서 실행할 때 설정할 수 있다.
     *
     * @param args a <code>String[]</code> value - command line
     * arguments
     */
    public static void main(String[] args) {

	int size = 15;
	if (args.length > 0)
	    size = Integer.parseInt(args[0]);

	JFrame frame = new JFrame();
	
	final int FRAME_WIDTH = 600;
	final int FRAME_HEIGHT = 650;
	frame.setSize(FRAME_WIDTH, FRAME_HEIGHT);
	frame.setTitle("Omok");
	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	
	OmokPanel panel = new OmokPanel(size);
	frame.add(panel);
	
	frame.setVisible(true);
    }
}

class OmokState {
	public static final int NONE = 0;
	public static final int BLACK = 1;
	public static final int WHITE = 2;
	private int size;
	private int winner;
	private int currentPlayer;
	private int board[][];
	public OmokState(int size) {
		this.size = size;
		board = new int[size][size];
		currentPlayer = BLACK;
	}
	
	public void playPiece(int row, int col) {
		if (validMove(row, col))
			board[row][col] = currentPlayer;
		else
			JOptionPane.showMessageDialog(null, "여기에 둘 수 없습니다.");
		
		switch (currentPlayer) {
		case BLACK:
			currentPlayer = WHITE;
			break;
		case WHITE:
			currentPlayer = BLACK;
			break;
		}
	}
	
	public int getPiece(int row, int col) {
		return board[row][col];
	}
	
	public int getWinner() {
		return winner;
	}
	
	public boolean validMove(int row, int col) {
		int r = row, c = col;
		/*
		 * step
		 * 수직: 0(북), 1(남)
		 * 수평: 2(동), 3(서)
		 * 사선: 4(동북), 5(서남), 6(서북), 7(동남)
		 */
		int step = 0;
		int[] stepCount = new int[8];
		boolean doneCheck = false;
		while (!doneCheck) {
			switch (step) {
			case 0:
				if (!outOfBounds(r-1) && sameColor(r--, c))
					stepCount[step]++;
				else { step++; r = row; c = col; }
				break;
			case 1:
				if (!outOfBounds(r+1) && sameColor(r++, c))
					stepCount[step]++;
				else { step++; r = row; c = col; }
				break;
			case 2:
				if (!outOfBounds(c+1) && sameColor(r, c++))
					stepCount[step]++;
				else { step++; r = row; c = col; }
				break;
			case 3:
				if (!outOfBounds(c-1) && sameColor(r, c--))
					stepCount[step]++;
				else { step++; r = row; c = col; }
				break;
			case 4:
				if (!outOfBounds(r-1) && !outOfBounds(c+1) && sameColor(r--, c++))
					stepCount[step]++;
				else { step++; r = row; c = col; }
				break;
			case 5:
				if (!outOfBounds(r+1) && !outOfBounds(c-1) && sameColor(r++, c--))
					stepCount[step]++;
				else { step++; r = row; c = col; }
				break;
			case 6:
				if (!outOfBounds(r-1) && !outOfBounds(c-1) && sameColor(r--, c--))
					stepCount[step]++;
				else { step++; r = row; c = col; }
				break;
			case 7:
				if (!outOfBounds(r+1) && !outOfBounds(c+1) && sameColor(r++, c++))
					stepCount[step]++;
				else { step++; r = row; c = col; }
				break;
			default:
				doneCheck = true;
				break;
			}
		}
		int result = moveResult(stepCount);
		if (result == 0) winner = currentPlayer;
		if (result == 1 || result == 2) return false;
		return true;
	}
	
	public boolean outOfBounds(int n) {
		return !(n >= 0 && n < size);
	}
	
	public boolean sameColor(int r, int c) {
		return board[r][c] == currentPlayer;
	}
	
	/*
	 * 이기는 수(5): 0
	 * 금수(33 혹은 44): 1
	 * 장목(6이상): 2
	 * 수: 3
	 */
	public int moveResult(int[] stepCount) {
		int countTwo = 0, countThree = 0;
		boolean win = false;
		for (int i=0; i<8; i++) {
			if (i % 2 == 1 && (stepCount[i-1] + stepCount[i] > 5)) return 2;
			else if (i % 2 == 1 && (stepCount[i-1] + stepCount[i] == 5)) win = true;
			if (stepCount[i] == 2) countTwo++;
			else if (stepCount[i] == 3) countThree++;
		}
		if (countTwo >= 2 || countThree >= 2) return 1;
		if (win) return 0;
		return 3;
	}
	
	
	
	
}

class OmokPanel extends JPanel
{
    private final int MARGIN = 5;
    private final double PIECE_FRAC = 0.9;

    private int size = 19;
    private OmokState state;
    
    public OmokPanel() 
    {
	this(15);
    }

    public OmokPanel(int size) 
    {
	super();
	this.size = size;
	state = new OmokState(size);
	addMouseListener(new GomokuListener());
    }

    class GomokuListener extends MouseAdapter 
    {
	public void mouseReleased(MouseEvent e) 
	{
	    double panelWidth = getWidth();
	    double panelHeight = getHeight();
	    double boardWidth = Math.min(panelWidth, panelHeight) - 2 * MARGIN;
	    double squareWidth = boardWidth / size;
	    double pieceDiameter = PIECE_FRAC * squareWidth;
	    double xLeft = (panelWidth - boardWidth) / 2 + MARGIN;
	    double yTop = (panelHeight - boardWidth) / 2 + MARGIN;
	    int col = (int) Math.round((e.getX() - xLeft) / squareWidth - 0.5);
	    int row = (int) Math.round((e.getY() - yTop) / squareWidth - 0.5);
	    if (row >= 0 && row < size && col >= 0 && col < size
		&& state.getPiece(row, col) == OmokState.NONE
		&& state.getWinner() == OmokState.NONE) {
		state.playPiece(row, col);
		repaint();
		int winner = state.getWinner();
		if (winner != OmokState.NONE)
		    JOptionPane.showMessageDialog(null,
                      (winner == OmokState.BLACK) ? "Black wins!" 
						    : "White wins!");
	    }
	}    
    }
    
    
    public void paintComponent(Graphics g) 
    {
	Graphics2D g2 = (Graphics2D) g;
	g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
			    RenderingHints.VALUE_ANTIALIAS_ON);
	
	double panelWidth = getWidth();
	double panelHeight = getHeight();

	g2.setColor(new Color(0.925f, 0.670f, 0.34f)); // 나무색
	g2.fill(new Rectangle2D.Double(0, 0, panelWidth, panelHeight));

	
	double boardWidth = Math.min(panelWidth, panelHeight) - 2 * MARGIN;
	double squareWidth = boardWidth / size;
	double gridWidth = (size - 1) * squareWidth;
	double pieceDiameter = PIECE_FRAC * squareWidth;
	boardWidth -= pieceDiameter;
	double xLeft = (panelWidth - boardWidth) / 2 + MARGIN;
	double yTop = (panelHeight - boardWidth) / 2 + MARGIN;

	g2.setColor(Color.BLACK);
	for (int i = 0; i < size; i++) {
	    double offset = i * squareWidth;
	    g2.draw(new Line2D.Double(xLeft, yTop + offset, 
				      xLeft + gridWidth, yTop + offset));
	    g2.draw(new Line2D.Double(xLeft + offset, yTop,
				      xLeft + offset, yTop + gridWidth));
	}
	
	for (int row = 0; row < size; row++) 
	    for (int col = 0; col < size; col++) {
		int piece = state.getPiece(row, col);
		if (piece != OmokState.NONE) {
		    Color c = (piece == OmokState.BLACK) ? Color.BLACK : Color.WHITE;
		    g2.setColor(c);
		    double xCenter = xLeft + col * squareWidth;
		    double yCenter = yTop + row * squareWidth;
		    Ellipse2D.Double circle
			= new Ellipse2D.Double(xCenter - pieceDiameter / 2,
					       yCenter - pieceDiameter / 2,
					       pieceDiameter,
					       pieceDiameter);
		    g2.fill(circle);
		    g2.setColor(Color.black);
		    g2.draw(circle);
		}
	    }
    }
}