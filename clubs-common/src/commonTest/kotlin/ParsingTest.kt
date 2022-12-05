import dev.gaabriel.clubs.common.parser.TextCommandParser
import dev.gaabriel.clubs.common.repository.CommandRepository
import dev.gaabriel.clubs.common.struct.ArgumentType
import dev.gaabriel.clubs.common.struct.CommandLiteralNode
import dev.gaabriel.clubs.common.util.*
import io.mockk.every
import io.mockk.mockk
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

public class ParsingTest  {
    private val repository = mockk<CommandRepository>()
    private val parser = TextCommandParser(
        dictionary = mockk(),
        repository = repository
    )

    @Test
    public fun `should parse single argument command`() {
        val sayCommand = genericCommand("say") {
            argument("message", ArgumentType.Text.Greedy) {}
        }
        every { repository.search("say", true) } returns sayCommand
        val parsingResult = parser.parse("+", "+say Hello, World!")

        assertTrue(parsingResult != null)
        assertEquals(sayCommand, parsingResult.root)
        assertTrue(parsingResult.root.children.size == 1)
        assertEquals(sayCommand.children.first(), parsingResult.node)
        assertEquals(sayCommand.children.size, parsingResult.arguments.size)
        assertEquals("Hello, World!", parsingResult.arguments.values.first())
    }

    @Test
    public fun `should parse nested arguments command`() {
        val testCommand = baseCommand("test") {
            arguments(quote(), integer(), long(), phrase()) { _, _, _, _ -> }
        }
        every { repository.search("test", true) } returns testCommand
        val parsingResult = parser.parse("+", """+test "Hello, World!" 123 456789 Hello, World!""")
        val parsingResultArgumentsValues = parsingResult?.arguments?.values?.toTypedArray()

        assertTrue(parsingResult != null)
        assertTrue(parsingResultArgumentsValues != null)
        assertEquals(testCommand, parsingResult.root)
        assertEquals(4, parsingResult.arguments.size)
        assertEquals("Hello, World!", parsingResultArgumentsValues[0])
        assertEquals(123, parsingResultArgumentsValues[1])
        assertEquals(456789L, parsingResultArgumentsValues[2])
        assertEquals("Hello, World!", parsingResultArgumentsValues[3])
    }

    @Test
    public fun `should parse nested arguments command with literals`() {
        val economyCommand = baseCommand("economy") {
            literal("pay") {
                argument("user", quote()) {
                    argument("amount", integer()) {
                        literal("express") {}
                    }
                }
            }
        }
        every { repository.search("economy", true) } returns economyCommand
        val parsingResult = parser.parse("+", """+economy pay "Gabriel" 500 express""")
        val parsingResultArgumentsValues = parsingResult?.arguments?.values?.toTypedArray()

        assertTrue(parsingResult != null)
        assertTrue(parsingResultArgumentsValues != null)
        assertEquals(economyCommand, parsingResult.root)
        assertEquals(2, parsingResult.arguments.size)
        assertEquals("Gabriel", parsingResultArgumentsValues[0])
        assertEquals(500, parsingResultArgumentsValues[1])
    }

    @Test
    public fun `should parse case sensitive command`() {
        parser.caseSensitive = true
        val talkCommand = baseCommand("talk") {
            literal("case", "CASE", "CaSe") {}
        }
        every { repository.search("talk", false) } returns talkCommand
        val lowercaseCaseParsingResult = parser.parse("+", """+talk case""")
        val uppercaseCaseParsingResult = parser.parse("+", """+talk CASE""")
        val mixedCaseParsingResult = parser.parse("+", """+talk CaSe""")

        assertTrue(lowercaseCaseParsingResult != null)
        assertTrue(uppercaseCaseParsingResult != null)
        assertTrue(mixedCaseParsingResult != null)

        // Lowercase
        assertEquals(talkCommand.children.size, lowercaseCaseParsingResult.root.children.size)
        assertEquals(talkCommand.children[0], lowercaseCaseParsingResult.node)
        assertEquals("case", (lowercaseCaseParsingResult.node as CommandLiteralNode).name)

        // Uppercase
        assertEquals(talkCommand.children.size, uppercaseCaseParsingResult.root.children.size)
        assertEquals(talkCommand.children[1], uppercaseCaseParsingResult.node)
        assertEquals("CASE", (uppercaseCaseParsingResult.node as CommandLiteralNode).name)

        // Mixed case
        assertEquals(talkCommand.children.size, mixedCaseParsingResult.root.children.size)
        assertEquals(talkCommand.children[2], mixedCaseParsingResult.node)
        assertEquals("CaSe", (mixedCaseParsingResult.node as CommandLiteralNode).name)
        parser.caseSensitive = false
    }
}