package ru.marxunion.atlassian.crowdtab.plugin;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.macro.Macro;
import com.atlassian.confluence.macro.MacroExecutionException;
import com.atlassian.crowd.embedded.api.CrowdService;
import com.atlassian.crowd.embedded.api.Group;
import com.atlassian.crowd.embedded.api.Query;
import com.atlassian.crowd.embedded.api.User;
import com.atlassian.crowd.search.EntityDescriptor;
import com.atlassian.crowd.search.builder.QueryBuilder;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class CrowdTableMacro implements Macro {

    private final CrowdService crowdService;


    @Autowired
    public CrowdTableMacro(@ComponentImport CrowdService crowdService) {
        this.crowdService = crowdService;
    }

    public String execute(Map<String, String> map, String bodyContent, ConversionContext conversionContext) throws MacroExecutionException {
        String groupName = map.get("groupName");
        List<User> users = StringUtils.isEmpty(groupName) ? findAllUsers() : findAllUsersByGroupName(groupName);

        return formatUserTable(users);
    }

    private List<User> findAllUsers() {
        Query<User> findUsersQuery = QueryBuilder.queryFor(User.class, EntityDescriptor.user()).returningAtMost(100);
        return StreamSupport.stream(crowdService.search(findUsersQuery).spliterator(), false)
                .collect(Collectors.toList());
    }

    private List<User> findAllUsersByGroupName(String groupName) {
        Group existGroup = crowdService.getGroup(groupName);
        if(existGroup == null) {
            throw new IllegalArgumentException("Group '" + groupName + "' doesn't exist");
        }

        Query<User> findUsersQuery = QueryBuilder.queryFor(User.class, EntityDescriptor.user())
                .childrenOf(EntityDescriptor.group()).withName(groupName)
                .returningAtMost(100);
        return StreamSupport.stream(crowdService.search(findUsersQuery).spliterator(), false)
                .collect(Collectors.toList());
    }

    private String formatUserTable(List<User> users) {
        StringBuilder builder = new StringBuilder();
        builder.append("<p>");
        if (!users.isEmpty()) {
            builder.append("<table width=\"50%\">");
            builder.append("<tr><th>Name</th><th>Displayed Name</th><th>isActive</th></tr>");
            for (User user: users) {
                builder.append("<tr>");
                builder.append("<td>").append(user.getName()).append("</td><td>")
                        .append(user.getDisplayName()).append("</td><td>")
                        .append(user.isActive()).append("</td>");
                builder.append("</tr>");
            }
            builder.append("</table>");
        } else {
            builder.append("You've done built yourself a macro! Nice work.");
        }
        builder.append("</p>");

        return builder.toString();
    }

    public BodyType getBodyType() { return BodyType.NONE; }

    public OutputType getOutputType() { return OutputType.BLOCK; }
}