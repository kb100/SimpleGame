import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.LinkedList;

public class QuadTree
{
    static final int MAX_LEVEL = 6;
    private Node root;
    public ArrayList<ArrayList<Drawable>> leaves;

    public QuadTree()
    {
        leaves = new ArrayList<ArrayList<Drawable>>(1024);
        root = new Node( 0, GameContent.GAME_WIDTH, 0, GameContent.GAME_HEIGHT, 0 );
    }

    public void add( Drawable drawable )
    {
        root.add( drawable );
    }

    public ArrayList< Drawable > getDrawablesInRegion( Rectangle region )
    {
        ArrayList< Drawable > drawables = new ArrayList< Drawable >();
        if( root.intersectsRectangle( region ) )
        {
            root.addDrawablesInRegion( region, drawables );
        }
        return drawables;
    }

    public ArrayList< Drawable > getDrawablesIntersecting( Drawable region )
    {
        ArrayList< Drawable > drawables = new ArrayList< Drawable >();
        if( root.intersectsDrawable( region ) )
        {
            root.addDrawablesInRegion( region, drawables );
        }
        return drawables;
    }
  
    private class Node
    {
        int level;
        int xMin, xMax;
        int yMin, yMax;
        Node upperLeft, upperRight, lowerLeft, lowerRight;
        ArrayList< Drawable > drawablesInRegion;

        public Node( int xMin, int xMax, int yMin, int yMax, int level )
        {
            this.xMin = xMin;
            this.xMax = xMax;
            this.yMin = yMin;
            this.yMax = yMax;
            this.level = level;
            if( level < MAX_LEVEL )
            {
                int xMid = xMin / 2 + xMax / 2;
                int yMid = yMin / 2 + yMax / 2;
                int nextLevel = level + 1;
                upperLeft = new Node( xMin, xMid, yMin, yMid, nextLevel );
                upperRight = new Node( xMid, xMax, yMin, yMid, nextLevel );
                lowerLeft = new Node( xMin, xMid, yMid, yMax, nextLevel );
                lowerRight = new Node( xMid, xMax, yMid, yMax, nextLevel );
            }
            else
            {
                // speed consideration
                // only the leaves have non-null arraylists
                drawablesInRegion = new ArrayList< Drawable >();
                leaves.add( drawablesInRegion );
            }
        }

        public boolean isLeaf()
        {
            return this.lowerLeft == null;
        }

        // Assumes it intersects the root
        public void addDrawablesInRegion( Rectangle region, ArrayList< Drawable > drawables )
        {
            if( this.isLeaf() )
            {
                drawables.addAll( drawablesInRegion );
            }
            else
            {
                if( upperLeft.intersectsRectangle( region ) )
                    upperLeft.addDrawablesInRegion( region, drawables );
                if( upperRight.intersectsRectangle( region ) )
                    upperRight.addDrawablesInRegion( region, drawables );
                if( lowerLeft.intersectsRectangle( region ) )
                    lowerLeft.addDrawablesInRegion( region, drawables );
                if( lowerRight.intersectsRectangle( region ) )
                    lowerRight.addDrawablesInRegion( region, drawables );
            }

        }

        // Assumes it intersects the root
        public void addDrawablesInRegion( Drawable region, ArrayList< Drawable > drawables )
        {
            if( this.isLeaf() )
            {
                drawables.addAll( drawablesInRegion );
            }
            else
            {
                if( upperLeft.intersectsDrawable( region ) )
                    upperLeft.addDrawablesInRegion( region, drawables );
                if( upperRight.intersectsDrawable( region ) )
                    upperRight.addDrawablesInRegion( region, drawables );
                if( lowerLeft.intersectsDrawable( region ) )
                    lowerLeft.addDrawablesInRegion( region, drawables );
                if( lowerRight.intersectsDrawable( region ) )
                    lowerRight.addDrawablesInRegion( region, drawables );
            }

        }

        public void add( Drawable drawable )
        {
            LinkedList< Node > queue = new LinkedList< Node >();
            queue.add( this );
            Node current;
            while( queue.size() > 0 )
            {
                current = queue.poll();
                if( current.intersectsDrawable( drawable ) )
                {
                    if( !current.isLeaf() )
                    {
                        queue.add( current.upperLeft );
                        queue.add( current.upperRight );
                        queue.add( current.lowerLeft );
                        queue.add( current.lowerRight );
                    }
                    else
                    {
                        // only leaves have non-null arraylists
                        current.drawablesInRegion.add( drawable );
                    }
                }
            }
        }

        public boolean intersectsDrawable( Drawable drawable )
        {
            return (drawable.x + drawable.width >= xMin && drawable.x < xMax && drawable.y + drawable.height >= yMin && drawable.y < yMax);
        }

        public boolean intersectsRectangle( Rectangle rectangle )
        {
            return (rectangle.x + rectangle.width >= xMin && rectangle.x < xMax && rectangle.y + rectangle.height >= yMin && rectangle.y < yMax);
        }

    }
}