import java.util.*

fun main() {
    val testInput = readInput("Day07_test")
    val testRoot = buildFileSystem(testInput)
    val testSizeCache = mutableMapOf<Node, Long>()
    check(part1(testRoot, testSizeCache) == 95437L)
    check(part2(testRoot, testSizeCache) == 24933642L)

    val input = readInput("Day07")
    val root = buildFileSystem(input)
    val sizeCache = mutableMapOf<Node, Long>()
    println(part1(root, sizeCache))
    println(part2(root, sizeCache))
}

private const val MAX = 100000L
private fun part1(root: Node, sizeCache: MutableMap<Node, Long>): Long {
    var result = 0L
    root.visit { node ->
        if (node.isDir) {
            val size = sizeCache.getOrPut(node, node::size)

            if (size < MAX) {
                result += size
            }
        }
    }

    return result
}

private const val TOTAL_SPACE = 70000000L
private const val TARGET_FREE_SPACE = 30000000L
private fun part2(root: Node, sizeCache: MutableMap<Node, Long>): Long {
    val rootSize = sizeCache.getOrPut(root, root::size)
    val spaceToFreeUp = TARGET_FREE_SPACE - (TOTAL_SPACE - rootSize)

    var result = Long.MAX_VALUE
    root.visit { node ->
        if (node.isDir) {
            val size = sizeCache.getOrPut(node, node::size)

            if (size >= spaceToFreeUp && size < result) {
                result = size
            }
        }
    }

    return result
}

private fun buildFileSystem(commands: List<String>): Node {
    val root = Node.Dir()
    val navigationStack = LinkedList<Node>()

    var i = 0
    while (i <= commands.lastIndex) {
        val cmd = commands[i++]

        when {
            cmd.startsWith("$ cd /") -> {
                navigationStack.clear()
                navigationStack.push(root)
            }
            cmd.startsWith("$ cd ..") -> navigationStack.pop()
            cmd.startsWith("$ cd ") -> {
                val destinationName = cmd.substringAfter("$ cd ")
                val destination = navigationStack.peek().getChild(destinationName)
                navigationStack.push(destination)
            }
            cmd.startsWith("$ ls") -> {
                val targetNode = navigationStack.peek()

                while (i <= commands.lastIndex && !commands[i].startsWith("$")) {
                    val item = commands[i++]
                    if (item.startsWith("dir")) {
                        targetNode.addChild(
                            name = item.substringAfter("dir "),
                            child = Node.Dir()
                        )
                    } else {
                        targetNode.addChild(
                            name = item.substringAfter(" "),
                            child = Node.File(
                                size = item.substringBefore(" ").toLong()
                            )
                        )
                    }
                }
            }
        }
    }

    return root
}

sealed interface Node {
    val isDir: Boolean
    fun getChild(name: String): Node
    fun addChild(name: String, child: Node)
    fun size(): Long
    fun visit(action: (Node) -> Unit)

    class Dir : Node {
        private val children = mutableMapOf<String, Node>()

        override val isDir: Boolean
            get() = true

        override fun getChild(name: String): Node = children[name] ?: error("Child $name not found")
        override fun addChild(name: String, child: Node) {
            children[name] = child
        }

        override fun size(): Long {
            return children.values.fold(0) { acc, node -> acc + node.size() }
        }

        override fun visit(action: (Node) -> Unit) {
            children.forEach { it.value.visit(action) }
            action(this)
        }
    }

    class File(val size: Long) : Node {
        override val isDir: Boolean
            get() = false

        override fun getChild(name: String): Node = error("getChild is not applicable for File")
        override fun addChild(name: String, child: Node) = error("addChild is not applicable for File")
        override fun size(): Long = size

        override fun visit(action: (Node) -> Unit) {
            action(this)
        }
    }
}