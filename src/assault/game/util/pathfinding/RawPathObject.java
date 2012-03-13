/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package assault.game.util.pathfinding;

import assault.game.util.GridManager;
import java.awt.Point;

/**
 *
 * @author matt
 */
public class RawPathObject {

	public Point[] points;//only int percision is needed

	public RawPathObject(PathFindingGridPoint startPoint, PathFindingGridPoint endPoint, PathFindingGridPoint[][] cameFromSet, GridManager gm, boolean[][] onPath) {
		System.out.println("constructing path...");
		if (startPoint != null && endPoint != null && cameFromSet != null && gm != null && onPath != null) {
			int length = 0;
			PathFindingGridPoint p = endPoint;
			//figure out the length
			while (p != startPoint) {
				if (p != null) {
					onPath[p.getX()][p.getY()] = true;
					//look at where p came from
					p = cameFromSet[p.getX()][p.getY()];
					length++;
				} else {
					//the path can't be made
					System.out.println("PathObject: path could not be constructed");
					points = new Point[0];
					return;
				}
			}
			System.out.println("length of path : " + length);
			points = new Point[length];

			//build it up from the end
			System.out.println("retracing to start...");
			p = endPoint;
			length--;//adj. for arr. handling
			while (p != startPoint && length > -1) {
				points[length] = new Point(gm.convGridToPixel(p.getX()), gm.convGridToPixel(p.getY()));
				System.out.println("point (" + gm.convGridToPixel(p.getX()) + "," + gm.convGridToPixel(p.getY()) + ") was added to the Pathobject");
				p = cameFromSet[p.getX()][p.getY()];
				length--;
			}
		} else {
			points = new Point[0];
			String outStr = "PathObject: path could not be constructed because ";
			if (startPoint == null) {
				outStr += "startPoint,";
			}
			if (endPoint == null) {
				outStr += "endPoint,";
			}
			if (cameFromSet == null) {
				outStr += "cameFromSet,";
			}
			if (gm == null) {
				outStr += "gm,";
			}
			if (onPath == null) {
				outStr += "onPath";
			}
			outStr += " were null.";
			System.out.println(outStr);
		}
		System.out.println("path constructed!");
	}
}
