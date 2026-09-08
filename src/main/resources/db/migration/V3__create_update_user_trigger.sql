CREATE TRIGGER audit_after_update
BEFORE UPDATE ON `user`
FOR EACH ROW
    UPDATE audit
    SET updated_at = now()
    WHERE user_id = NEW.id;


