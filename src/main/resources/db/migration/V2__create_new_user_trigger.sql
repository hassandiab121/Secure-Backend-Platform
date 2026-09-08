CREATE TRIGGER audit_user_after_insert
AFTER INSERT ON `user`
FOR EACH ROW
INSERT INTO `audit` (`user_id`, `created_at`, `updated_at`)
     VALUES (NEW.`id`, now(), now())


