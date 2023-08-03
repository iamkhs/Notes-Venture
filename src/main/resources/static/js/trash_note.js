let activePopup = null; // Store the currently active popup


// Add event listener to all note cards to open the update note popup
const noteLinks = document.querySelectorAll(".note-link");
noteLinks.forEach((noteLink) => {
    noteLink.addEventListener("click", function (event) {
        event.preventDefault();
        hideActivePopup(); // Close any active popup before showing the update note popup
        showUpdatePopup(noteLink);
    });
});

function showUpdatePopup(noteLink) {
    document.querySelector("#note-id").value = noteLink.getAttribute("data-noteid");

    document.querySelector("#update-note-popup").style.display = "flex";
    activePopup = "update-note-popup";
}

// Function to hide the update note popup
function hideUpdatePopup() {
    document.querySelector("#update-note-popup").style.display = "none";
    activePopup = null;
}

// Function to hide the active popup if there is one
function hideActivePopup() {
    if (activePopup === "create-note-popup") {
        hidePopup();
    } else if (activePopup === "update-note-popup") {
        hideUpdatePopup();
    }
}

document.querySelector("#update-note-popup").addEventListener("click", function (event) {
    if (event.target === this) {
        hideUpdatePopup();
    }
});
