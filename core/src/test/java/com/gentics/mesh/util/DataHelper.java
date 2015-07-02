package com.gentics.mesh.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.gentics.mesh.core.data.User;
import com.gentics.mesh.core.data.Role;
import com.gentics.mesh.core.data.node.Node;
import com.gentics.mesh.core.data.relationship.Permission;
import com.gentics.mesh.core.data.root.UserRoot;
import com.gentics.mesh.core.data.service.NodeService;
import com.gentics.mesh.core.data.service.UserService;
import com.gentics.mesh.core.data.service.RoleService;

/**
 * Various helper methods that can be used to setup test data.
 * 
 * @author johannes2
 *
 */
@Component
public class DataHelper {

	@Autowired
	private RoleService roleService;

	@Autowired
	private NodeService nodeService;

	@Autowired
	private UserService userService;

	public Node addNode(Node parentNode, String name, Role role, Permission... perms) {
		Node node = parentNode.create();
		for (Permission perm : perms) {
			role.addPermissions(node, perm);
		}
		return node;
	}

	public User addUser(UserRoot root, String name, Role role, Permission... perms) {
		
		User user = root.create("extraUser");
		for (Permission perm : perms) {
			role.addPermissions(user, perm);
		}
		return user;
	}
}
