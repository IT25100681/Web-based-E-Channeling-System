import { useState } from "react";

/**
 * SettingsAndOperations
 *
 * Executes the backup workflow for the hospital-management module:
 * 1. POST /api/system-operations/record-backup to create the audit log/status.
 * 2. GET /api/system-operations/download-backup to stream the JSON export.
 * 3. Converts the response to a Blob and triggers a browser download.
 *
 * The component expects an optional `onToast(message, variant)` callback where
 * variant is "success" or "danger".
 */
export default function SettingsAndOperations({ onToast = () => {} }) {
  const [isDownloadingBackup, setIsDownloadingBackup] = useState(false);

  async function handleBackupDownload() {
    setIsDownloadingBackup(true);

    try {
      const recordResponse = await fetch("/api/system-operations/record-backup", {
        method: "POST",
      });

      if (!recordResponse.ok) {
        throw new Error("Could not record backup checkpoint.");
      }

      const backupResponse = await fetch("/api/system-operations/download-backup");

      if (!backupResponse.ok) {
        throw new Error("Could not generate backup export.");
      }

      const blob = await backupResponse.blob();
      const disposition = backupResponse.headers.get("Content-Disposition") || "";
      const filenameMatch = disposition.match(/filename="?([^"]+)"?/);
      const filename = filenameMatch?.[1] || "hospital_system_backup.json";
      const url = window.URL.createObjectURL(blob);
      const link = document.createElement("a");

      link.href = url;
      link.download = filename;
      document.body.appendChild(link);
      link.click();
      link.remove();
      window.URL.revokeObjectURL(url);

      onToast("Backup file downloaded.", "success");
    } catch (error) {
      onToast(error.message || "Backup export failed.", "danger");
    } finally {
      setIsDownloadingBackup(false);
    }
  }

  return (
    <button
      type="button"
      className="btn btn-outline-primary btn-sm w-100"
      onClick={handleBackupDownload}
      disabled={isDownloadingBackup}
    >
      {isDownloadingBackup ? "Preparing backup..." : "Download Backup (JSON)"}
    </button>
  );
}
