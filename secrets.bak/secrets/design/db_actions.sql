-- CREATE PROJECTS
INSERT INTO `projects` (`slug`, `name`, `description`) VALUES
  ('example-app', 'Example App', 'Demo project for testing'),
  ('mobile-api', 'Mobile API', 'Backend for mobile clients'),
  ('internal-tools', 'Internal Tools', 'Utilities and scripts'),
  ('website', 'Public Website', 'Marketing site and CMS');

-- UPDATE PROJECT BY SLUG
UPDATE projects SET slug='public-web' where slug='website';

-- Verify inserts
SELECT id, slug, name, description, created_at, updated_at FROM `projects` ORDER BY id;
