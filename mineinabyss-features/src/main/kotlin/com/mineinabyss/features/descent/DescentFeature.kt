package com.mineinabyss.features.descent

import com.mineinabyss.components.descent.DescentContext
import com.mineinabyss.geary.papermc.tracking.entities.toGeary
import com.mineinabyss.idofront.commands.execution.stopCommand
import com.mineinabyss.idofront.commands.extensions.actions.playerAction
import com.mineinabyss.idofront.messaging.info
import com.mineinabyss.idofront.plugin.listeners
import com.mineinabyss.mineinabyss.core.AbyssFeature
import com.mineinabyss.mineinabyss.core.MineInAbyssPlugin
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

class DescentFeature : AbyssFeature {
    override fun MineInAbyssPlugin.enableFeature() {
        listeners(
            DescentListener()
        )

        commands {
            mineinabyss command@{
                "start" {
                    playerAction {
                        player.toGeary().apply {
                            if (has<DescentContext>())
                                this@command.stopCommand("You are already ingame!\nYou can leave using /stopdescent")
                            setPersisting(DescentContext())
                        }
//                    GondolaGUI(player).show(player)
                    }
                }
                "stopdescent" {
                    playerAction {
                        with(player.toGeary()) {
                            val descent = get<DescentContext>()
                                ?: this@command.stopCommand("You are not currently ingame!\nStart by using /start")
                            if (!descent.confirmedLeave) {
                                descent.confirmedLeave = true
                                sender.info(
                                    """
                        <red>You are about to leave the game!!!
                        <b>Your progress will be lost</b>, but any xp and money you earned will stay with you.
                        Type /stopdescent again to leave.
                        """.trimIndent()
                                )
                            } else {
                                player.health = 0.0
                                player.removeDescentContext()
                            }
                        }
                    }
                }
            }
        }
    }
}
