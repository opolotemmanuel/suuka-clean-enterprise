import type { Role } from '../data/roleActivities';

type Props = { role: Role };

export default function SettingsPage({ role }: Props) {
  if (role !== 'admin' && role !== 'executive-admin') {
    return (
      <div className="panel-card">
        <h3>Settings</h3>
        <p>Access denied.</p>
      </div>
    );
  }

  return (
    <div className="stacked-content">
      <section className="panel-card">
        <div className="panel-header">
          <h3>System Settings</h3>
        </div>
      <div className="settings-grid">
        <div className="settings-panel">
          <h4>General</h4>
          <label>Site Name</label>
          <input type="text" defaultValue="Suuka Cleaning Marketplace" />
          <div className="field-group">
            <label>Default Currency</label>
            <select defaultValue="USD">
              <option>USD</option>
              <option>EUR</option>
              <option>KES</option>
            </select>
          </div>
        </div>

        <div className="settings-panel">
          <h4>Security</h4>
          <label>Password Policy</label>
          <select defaultValue="medium">
            <option value="low">Low</option>
            <option value="medium">Medium</option>
            <option value="high">High</option>
          </select>

          <div className="field-group">
            <label>Two-factor Authentication</label>
            <div className="radio-row">
              <label><input type="radio" name="mfa" defaultChecked /> Optional</label>
              <label><input type="radio" name="mfa" /> Required</label>
            </div>
          </div>
        </div>
      </div>

      <div className="form-actions">
        <button className="primary-button">Save Settings</button>
      </div>
      </section>
    </div>
  );
}
