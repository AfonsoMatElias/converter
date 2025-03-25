package io.github.afonsomatelias.Profiles;

import io.github.afonsomatelias.Configurations.Profile;
import io.github.afonsomatelias.Models.User;
import io.github.afonsomatelias.Models.UserDto;

public class UserProfile extends Profile {
	@Override
	public void init() {
		createMap(User.class, UserDto.class)
			.skipMember("password");
	}
}
