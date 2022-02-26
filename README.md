![GitHub Workflow Status](https://img.shields.io/github/workflow/status/SrGaabriel/clubs/Build)
![GitHub issues](https://img.shields.io/github/issues/SrGaabriel/clubs)
![GitHub Repo stars](https://img.shields.io/github/stars/SrGaabriel/clubs)

# ♣️ clubs

A simple and versatile command framework made with the primary objective of making `Guilded` command experience a little more elegant.

The default implementation of the `common` module is the `client`, which uses [deck](https://github.com/SrGaabriel/deck) to communicate with Guilded's API.

## Documentation

There is no official documentation, but you can find a nice example of the structure here:

#### common

This is module containing the main functionalities of the framework, being less powerful than the others:

```kotlin
val command = newCommand("repeat", "repeattask") {
    // Integer type
    val times by integer("times")
    // Nullable string type, only accepts a word
    val exampleWord by word("word").optional()
    // Nullable string type, only accepts text between quotes ("")
    val exampleQuotedString by quote("quote").optional()
    // Nullable string type, accepts unlimited words, but must be the last argument
    val exampleGreedyString by text("greedy_string").optional()
    runs {
        repeat(times) {
            send("$times")
            send(exampleWord)
            send(exampleQuotedString)
            send(exampleGreedyString)
        }
    }
    // Subcommand
    command("massive") {
        val times by long("times")
        runs {
            repeat(times) { count ->
                 send("Count: $count")
            }
        }
    }
}
```

You can't use this module alone, since it does not have a command handler, in other words, it doesn't have a bridge to send and receive content from, so it can't reply nor parse commands.

#### client

This is the framework's guilded client API implementation, supporting more data types and structures, take a look at the example:

```kotlin
suspend fun main() {
    val deck = DeckClient {}
    val clubs: ClubsInstance = ClientClubsInstance()
    clubs.register(HelloCommand())
    deck.login()
    clubs.start(deck)
}

private fun HelloCommand() = command("hello") {
    val user by user("user")
    val role by role("role")
    val channel by channel("channel")
    runs {
        this.channel.sendContent {
            paragraph {
                + "Channel: "
                + channel
            }
            paragraph {
                + "User: "
                + user
            }
            paragraph {
                + "Role: "
                + role
            }
        }
    }
}
```

The above example accepts user, role and channel mentions and sends a reply mentioning them again.

If you want to get the channel's state (like name, topic etc...), just like with `deck`, you can use `channel.getState()` to fetch this data making a request to guilded's API.