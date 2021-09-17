package chess

fun main() {
    val chess = Chess()

    println("Pawns-Only Chess")

    println("First Player's name:")
    val name1 = readLine()!!
    println("Second Player's name:")
    val name2 = readLine()!!

    var isWhite = true

    chess.drawBoard()
    while (chess.isGame) {
        println("${if (isWhite) name1 else name2}'s turn:")
        val cmd = readLine()!!
        if (cmd == "exit") {
            chess.isGame = false
            break
        } else {
            val state = chess.doMove(isWhite, cmd)
            val st = state.substringBefore(',')
            val msg = state.substringAfter(',')
            when (st) {
                "end" -> {
                    chess.drawBoard()
                    println(msg)
                    break
                }
                "fail" -> println(msg)
                else -> {
                    chess.drawBoard()
                    isWhite = !isWhite
                }
            }
        }
    }
    println("Bye!")
}

class Chess {

    val pawns = Array(8) { Array(8) { 0 } }
    var cntWhite = 8
    var cntBlack = 8
    var oldMove = "a7a7"
    var isGame = true

    init {
        pawns[1] = Array(8) { 1 }  // Black
        pawns[6] = Array(8) { -1 } // White
    }

    fun drawBoard() {
        val s = "W B"
        for (row in 0..7) {
            println("  +---+---+---+---+---+---+---+---+")
            print("${8 - row} |")
            for (col in 0..7) {
                print(" ${s[pawns[row][col] + 1]} |")
            }
            println()
        }
        println("  +---+---+---+---+---+---+---+---+")
        println("    a   b   c   d   e   f   g   h")
        println()
    }

    fun doMove(isWhite: Boolean, cmd: String): String {
        val pattern = Regex("[a-h][1-8][a-h][1-8]")
        var res = cmd.matches(pattern)
        if (!res) {
            return "fail, Invalid Input"
        }
        val r1 = '8' - cmd[1]
        val c1 = cmd[0] - 'a'
        val r2 = '8' - cmd[3]
        val c2 = cmd[2] - 'a'
        val firstLine = if (isWhite) 6 else 1
        val v = if (isWhite) -1 else 1
        val color = if (isWhite) "white" else "black"

        if (pawns[r1][c1] != v ) {
            return "fail,No $color pawn at ${cmd[0]}${cmd[1]}"
        }
        res = false
        when {
//         ход на свободное поле
            (r2 == r1 + v || r1 == firstLine && r2 == r1 + v + v) && c2 == c1 ->
                res = pawns [r2][c2] == 0
                    && (r2 == r1 + v || r1 == firstLine && r2 == r1 + v + v)
                    && c2 == c1
//        атака
            r2 == r1 + v && (c2 == c1 + 1 || c2 == c1 - 1) -> {
                if (pawns[r2][c2] == - v) {
                    res = true
                    if (isWhite) cntBlack-- else cntWhite--
                } else {
                    val r2old = '8' - oldMove[3]
                    val c2old = oldMove[2] - 'a'
                    res = r2 == firstLine + v * 4 && r2 == r2old + v && c2 == c2old
                    if (res) {
                        pawns[r2old][c2old] = 0
                        if (isWhite) cntBlack-- else cntWhite--
                    }
                }
            }
        }
        if (res) {
            pawns[r2][c2] = pawns[r1][c1]
            pawns[r1][c1] = 0
            oldMove = cmd
        }

        if (r2 == 0 || r2 == 7 || cntWhite == 0 || cntBlack == 0) {
            isGame = false
            return "end,${color.capitalize()} Wins!"
        }
        if (isDraw(isWhite)) return "end,Stalemate!"
        return if (res) "next" else "fail,Invalid Input"
    }

    fun isDraw(isWhite: Boolean): Boolean {
        val v = if (isWhite) 1 else -1
        for (r in 1..6) {
            for (c in 0..7) {
                if (pawns[r][c] == v) {
                    if (pawns[r][c] == v && pawns[r + v][c] == 0 ||
                        c in 1..7 && pawns[r][c] == v && pawns[r+v][c - 1] == -v ||
                        c in 0..6 && pawns[r][c] == v && pawns[r+v][c + 1] == -v
                    ) {
                        return false
                    }
                }
            }
        }
        return true
    }

}