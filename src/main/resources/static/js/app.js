
document.addEventListener("DOMContentLoaded", () => {
    // Confirm before submitting forms marked as destructive.
    document.querySelectorAll("form[data-confirm]").forEach((form) => {
        form.addEventListener("submit", (event) => {
            const message = form.dataset.confirm || "Are you sure?";

            if (!window.confirm(message)) {
                event.preventDefault();
            }
        });
    });

    // Prevent accidental double submission.
    document.querySelectorAll("form[data-disable-submit]").forEach((form) => {
        form.addEventListener("submit", () => {
            const button = form.querySelector('button[type="submit"]');

            if (button && form.checkValidity()) {
                button.disabled = true;
                button.dataset.originalText = button.textContent;
                button.textContent = "Please wait...";
            }
        });
    });
});
