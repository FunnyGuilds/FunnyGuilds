# Release Publishing Setup

This document describes how to set up automatic publishing of FunnyGuilds releases to CurseForge, Modrinth, and Paper Hangar.

## Overview

The `.github/workflows/release-publish.yml` workflow automatically publishes new releases to multiple platforms when a new GitHub release is published.

## Required Secrets

The following GitHub repository secrets must be configured for the workflow to function:

### CurseForge
- **`CURSEFORGE_PROJECT_ID`**: Your CurseForge project ID (numeric ID from the project page URL)
- **`CURSEFORGE_TOKEN`**: CurseForge API token
  - Get from: https://www.curseforge.com/account/api-tokens
  - Requires "Upload" permission

### Modrinth
- **`MODRINTH_PROJECT_ID`**: Your Modrinth project ID (slug from project URL)
- **`MODRINTH_TOKEN`**: Modrinth API token
  - Get from: https://modrinth.com/settings/pats
  - Requires "Create version" and "Upload version" scopes

### Paper Hangar
- **`HANGAR_PROJECT_ID`**: Your Hangar project ID (slug from project URL, e.g., "YourName/FunnyGuilds")
- **`HANGAR_TOKEN`**: Hangar API token
  - Get from: https://hangar.papermc.io/auth/settings/api-keys
  - Requires appropriate permissions for uploading versions

## How to Configure Secrets

1. Go to your GitHub repository settings
2. Navigate to **Settings > Secrets and variables > Actions**
3. Click **New repository secret**
4. Add each secret listed above with its corresponding value

## How to Create a Release

1. Create a new tag for your release:
   ```bash
   git tag -a v4.13.1 -m "Release v4.13.1"
   git push origin v4.13.1
   ```

2. Go to GitHub and create a new release from the tag
3. Fill in the release title and description (changelog)
4. Publish the release

The workflow will automatically:
- Build the plugin
- Upload to CurseForge, Modrinth, and Hangar
- Use the release title, tag, and changelog from GitHub

## Supported Platforms

The plugin is published with support for:
- **Game Versions**: Minecraft 1.8 - 1.21.4
- **Loaders**: Bukkit, Spigot, Paper, Purpur
- **Dependencies**: WorldEdit, WorldGuard, Vault, PlaceholderAPI, HolographicDisplays, DecentHolograms, Multiverse-Core, dynmap (all optional)

## Troubleshooting

### Workflow fails on one platform
The workflow is configured with `fail-mode: skip`, so it will continue publishing to other platforms even if one fails.

### Missing secrets
If a platform's secrets are not configured, that platform will be skipped automatically.

### Build fails
Check the Gradle build step in the workflow run logs. Ensure the build succeeds locally before creating a release.

## Additional Resources

- [mc-publish Documentation](https://github.com/Kir-Antipov/mc-publish)
- [Paper Hangar Publishing Guide](https://docs.papermc.io/misc/hangar-publishing)
- [CurseForge API Documentation](https://support.curseforge.com/en/support/solutions/articles/9000197321-curseforge-api)
- [Modrinth API Documentation](https://docs.modrinth.com/)
