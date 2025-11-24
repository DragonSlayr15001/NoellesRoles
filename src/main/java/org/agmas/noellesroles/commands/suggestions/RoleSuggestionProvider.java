package org.agmas.noellesroles.commands.suggestions;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import dev.doctor4t.trainmurdermystery.api.TMMRoles;
import net.minecraft.command.CommandSource;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.Identifier;
import org.agmas.noellesroles.Noellesroles;

import java.util.concurrent.CompletableFuture;

public class RoleSuggestionProvider implements SuggestionProvider<ServerCommandSource> {
    @Override
    public CompletableFuture<Suggestions> getSuggestions(CommandContext<ServerCommandSource> context, SuggestionsBuilder builder) throws CommandSyntaxException {
        // suggest all roles
        if (TMMRoles.ROLES.isEmpty()) return Suggestions.empty();

        for (TMMRoles.Role role : TMMRoles.ROLES ) {
            Identifier roleId = role.identifier();
            if (Noellesroles.VANNILA_ROLE_IDS.contains(roleId)) continue;
            if (roleId != null && CommandSource.shouldSuggest(builder.getRemaining(), roleId.getPath())) {
                builder.suggest(roleId.getPath());
            }
        }
        return builder.buildFuture();
    }
}
