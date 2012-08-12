import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.io.Serializable;
import java.util.ArrayDeque;
import java.util.ArrayList;

public class QuadTree implements Serializable
{
    static final int MAX_LEVEL = 9;
    static final int BRANCHING_LIMIT = 32;
    static final int MAX_NUM_LEAVES = 1 << MAX_LEVEL;
    static final int WIDTH = GameContent.GAME_WIDTH;
    static final int HEIGHT = GameContent.GAME_HEIGHT;

    private Node root;
    ArrayDeque<ArrayList<Drawable>> leaves;

    ArrayDeque<Node> leafNodes;

    public QuadTree()
    {
        leaves = new ArrayDeque<ArrayList<Drawable>>(MAX_NUM_LEAVES);
        leafNodes = new ArrayDeque<Node>(MAX_NUM_LEAVES);
        root = Node.Mempool.checkout(0, WIDTH, 0, HEIGHT, 0);
        ArrayDeque<Node> stack = new ArrayDeque<Node>();
        stack.add(root);
        Node currentNode;
        int xMin, xMid, xMax, yMin, yMid, yMax, nextLevel;
        while((currentNode = stack.pollLast()) != null)
        {
            if(currentNode.level < MAX_LEVEL)
            {
                xMin = currentNode.xMin;
                xMax = currentNode.xMax;
                xMid = currentNode.xMin / 2 + currentNode.xMax / 2;

                yMin = currentNode.yMin;
                yMax = currentNode.yMax;
                yMid = currentNode.yMin / 2 + currentNode.yMax / 2;

                nextLevel = currentNode.level + 1;

                currentNode.upperLeft = Node.Mempool.checkout(xMin, xMid, yMin, yMid, nextLevel);
                currentNode.upperRight = Node.Mempool.checkout(xMid, xMax, yMin, yMid, nextLevel);
                currentNode.lowerLeft = Node.Mempool.checkout(xMin, xMid, yMid, yMax, nextLevel);
                currentNode.lowerRight = Node.Mempool.checkout(xMid, xMax, yMid, yMax, nextLevel);
            }
            else
            {
                // speed consideration
                // only the leaves have non-null ArrayDeques
                currentNode.drawablesInRegion = new ArrayList<Drawable>();
                leaves.addLast(currentNode.drawablesInRegion);
                leafNodes.addLast(currentNode);
            }
        }
    }

    public QuadTree(QuadTree old)
    {
        leaves = new ArrayDeque<ArrayList<Drawable>>((int)(old.leaves.size() * 1.2));
        leafNodes = new ArrayDeque<Node>((int)(old.leaves.size() * 1.2));
        Node oldCurrentNode = old.root;
        root = Node.Mempool.checkout(oldCurrentNode.xMin, oldCurrentNode.xMax, oldCurrentNode.yMin, oldCurrentNode.yMax, oldCurrentNode.level);
        ArrayDeque<Node> stack = new ArrayDeque<Node>();
        ArrayDeque<Node> oldStack = new ArrayDeque<Node>();
        stack.add(root);
        oldStack.add(oldCurrentNode);
        Node currentNode;
        int xMin, xMid, xMax, yMin, yMid, yMax, nextLevel;

        while((currentNode = stack.pollLast()) != null)
        {
            oldCurrentNode = oldStack.pollLast();
            if(oldCurrentNode != Node.NULL_NODE && oldCurrentNode.level < MAX_LEVEL && oldCurrentNode.drawableCount > BRANCHING_LIMIT)
            {
                xMin = oldCurrentNode.xMin;
                xMax = oldCurrentNode.xMax;
                xMid = oldCurrentNode.xMin / 2 + oldCurrentNode.xMax / 2;

                yMin = oldCurrentNode.yMin;
                yMax = oldCurrentNode.yMax;
                yMid = oldCurrentNode.yMin / 2 + oldCurrentNode.yMax / 2;

                nextLevel = oldCurrentNode.level + 1;

                currentNode.upperLeft = Node.Mempool.checkout(xMin, xMid, yMin, yMid, nextLevel);
                currentNode.upperRight = Node.Mempool.checkout(xMid, xMax, yMin, yMid, nextLevel);
                currentNode.lowerLeft = Node.Mempool.checkout(xMin, xMid, yMid, yMax, nextLevel);
                currentNode.lowerRight = Node.Mempool.checkout(xMid, xMax, yMid, yMax, nextLevel);

                stack.addLast(currentNode.upperLeft);
                stack.addLast(currentNode.upperRight);
                stack.addLast(currentNode.lowerLeft);
                stack.addLast(currentNode.lowerRight);

                if(oldCurrentNode.isLeaf())
                {
                    // will add new leaves that did not exist in the old tree
                    oldStack.addLast(Node.NULL_NODE);
                    oldStack.addLast(Node.NULL_NODE);
                    oldStack.addLast(Node.NULL_NODE);
                    oldStack.addLast(Node.NULL_NODE);

                    // save allocation by stealing old node's ArrayList
                    // clearing is O(n), but garbage collector would do this anyway
                    currentNode.drawablesInRegion = oldCurrentNode.drawablesInRegion;
                    currentNode.drawablesInRegion.clear();
                }
                else
                {
                    oldStack.addLast(oldCurrentNode.upperLeft);
                    oldStack.addLast(oldCurrentNode.upperRight);
                    oldStack.addLast(oldCurrentNode.lowerLeft);
                    oldStack.addLast(oldCurrentNode.lowerRight);

                    Node.Mempool.returnNode(oldCurrentNode);
                }
            }
            else
            {
                if(oldCurrentNode != Node.NULL_NODE)
                {
                    Node.Mempool.returnNodeTree(oldCurrentNode);
                    if(oldCurrentNode.isLeaf())
                    {
                        // save allocation by stealing old node's ArrayList
                        // clearing is O(n), but garbage collector would do this anyway
                        currentNode.drawablesInRegion = oldCurrentNode.drawablesInRegion;
                        currentNode.drawablesInRegion.clear();
                    }
                }

                // speed consideration
                // only the leaves have non-null ArrayLists
                // check to see if already stole old's ArrayList
                if(currentNode.drawablesInRegion == null)
                    currentNode.drawablesInRegion = new ArrayList<Drawable>();

                leaves.addLast(currentNode.drawablesInRegion);
                leafNodes.addLast(currentNode);
            }
        }
    }

    public void draw(Graphics g)
    {
        g.setColor(Color.gray);
        for(Node node : leafNodes)
        {
            g.drawRect(node.xMin, node.yMin, node.xMax - node.xMin, node.yMax - node.yMin);
        }
    }

    public void add(Drawable drawable)
    {
        root.add(drawable);
    }

    public ArrayDeque<Drawable> getDrawablesInRegion(Rectangle region)
    {
        ArrayDeque<Drawable> drawables = new ArrayDeque<Drawable>();
        if(root.intersectsRectangle(region))
        {
            root.addDrawablesInRegion(region, drawables);
        }
        return drawables;
    }

    public ArrayDeque<Drawable> getDrawablesIntersecting(Drawable region)
    {
        ArrayDeque<Drawable> drawables = new ArrayDeque<Drawable>();
        if(root.intersectsDrawable(region))
        {
            root.addDrawablesInRegion(region, drawables);
        }
        return drawables;
    }

    private static class Node implements Serializable
    {
        static final Node NULL_NODE = new Node(0, 0, 0, 0, 0);

        int level;
        int xMin, xMax;
        int yMin, yMax;
        Node upperLeft, upperRight, lowerLeft, lowerRight;
        int drawableCount;
        ArrayList<Drawable> drawablesInRegion;

        private Node(int xMin, int xMax, int yMin, int yMax, int level)
        {
            this.xMin = xMin;
            this.xMax = xMax;
            this.yMin = yMin;
            this.yMax = yMax;
            this.level = level;
            drawableCount = 0;
            // children and drawablesInRegion null, must be set by caller
        }

        public boolean isLeaf()
        {
            return this.lowerLeft == null;
        }

        // Assumes it intersects the root
        public void addDrawablesInRegion(Rectangle region, ArrayDeque<Drawable> drawables)
        {
            if(this.isLeaf())
            {
                drawables.addAll(drawablesInRegion);
            }
            else
            {
                if(upperLeft.intersectsRectangle(region))
                    upperLeft.addDrawablesInRegion(region, drawables);
                if(upperRight.intersectsRectangle(region))
                    upperRight.addDrawablesInRegion(region, drawables);
                if(lowerLeft.intersectsRectangle(region))
                    lowerLeft.addDrawablesInRegion(region, drawables);
                if(lowerRight.intersectsRectangle(region))
                    lowerRight.addDrawablesInRegion(region, drawables);
            }
        }

        // Assumes it intersects the root
        public void addDrawablesInRegion(Drawable region, ArrayDeque<Drawable> drawables)
        {
            if(this.isLeaf())
            {
                drawables.addAll(drawablesInRegion);
            }
            else
            {
                if(upperLeft.intersectsDrawable(region))
                    upperLeft.addDrawablesInRegion(region, drawables);
                if(upperRight.intersectsDrawable(region))
                    upperRight.addDrawablesInRegion(region, drawables);
                if(lowerLeft.intersectsDrawable(region))
                    lowerLeft.addDrawablesInRegion(region, drawables);
                if(lowerRight.intersectsDrawable(region))
                    lowerRight.addDrawablesInRegion(region, drawables);
            }

        }

        public void add(Drawable drawable)
        {
            ArrayDeque<Node> stack = new ArrayDeque<Node>();
            stack.addLast(this);
            Node current;
            while((current = stack.pollLast()) != null)
            {
                if(current.intersectsDrawable(drawable))
                {
                    current.drawableCount++;
                    if(!current.isLeaf())
                    {
                        stack.addLast(current.upperLeft);
                        stack.addLast(current.upperRight);
                        stack.addLast(current.lowerLeft);
                        stack.addLast(current.lowerRight);
                    }
                    else
                    {
                        // only leaves have non-null ArrayLists
                        current.drawablesInRegion.add(drawable);
                    }
                }
            }
        }

        public boolean intersectsDrawable(Drawable drawable)
        {
            return(drawable.x + drawable.width >= xMin && drawable.x < xMax && drawable.y + drawable.height >= yMin && drawable.y < yMax);
        }

        public boolean intersectsRectangle(Rectangle rectangle)
        {
            return(rectangle.x + rectangle.width >= xMin && rectangle.x < xMax && rectangle.y + rectangle.height >= yMin && rectangle.y < yMax);
        }

        public static class Mempool
        {
            private static ArrayDeque<Node> available = new ArrayDeque<Node>(2048);
            private static int nodeCount = 2048;

            public static Node checkout(int xMin, int xMax, int yMin, int yMax, int level)
            {
                if(available.isEmpty())
                    makeMoreNodes();

                Node toCheckout = available.pollLast();
                toCheckout.xMin = xMin;
                toCheckout.xMax = xMax;
                toCheckout.yMin = yMin;
                toCheckout.yMax = yMax;
                toCheckout.level = level;
                toCheckout.drawableCount = 0;
                toCheckout.drawablesInRegion = null;
                toCheckout.upperLeft = null;
                toCheckout.upperRight = null;
                toCheckout.lowerLeft = null;
                toCheckout.lowerRight = null;

                return toCheckout;
            }

            private static void makeMoreNodes()
            {
                for(int i = 0; i < 1024; ++i)
                    available.addLast(new Node(0, 0, 0, 0, 0));

                nodeCount += 1024;
            }

            public static void returnNode(Node toReturn)
            {
                available.addLast(toReturn);
            }

            public static void returnNodeTree(Node toReturn)
            {
                ArrayDeque<Node> stack = new ArrayDeque<Node>();
                stack.add(toReturn);
                while((toReturn = stack.pollLast()) != null)
                {
                    available.addLast(toReturn);
                    if(!toReturn.isLeaf())
                    {
                        stack.addLast(toReturn.upperLeft);
                        stack.addLast(toReturn.upperRight);
                        stack.addLast(toReturn.lowerLeft);
                        stack.addLast(toReturn.lowerRight);
                    }
                }
            }
        }
    }
}
