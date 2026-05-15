function renderFooter() {

    // Select footer container
    const footer = document.getElementById("footer");

    // Stop execution if footer container does not exist
    if (!footer) {
        return;
    }

    // Inject footer HTML
    footer.innerHTML = `
        <footer class="footer">

            <div class="footer-container">

                <!-- Logo Section -->
                <div class="footer-logo">
                    <img 
                        src="../assets/images/logo/logo.png" 
                        alt="Hospital CMS Logo"
                        class="footer-logo-img"
                    />

                    <p>
                        © Copyright 2026.
                        All Rights Reserved by Smart Clinic.
                    </p>
                </div>

                <!-- Footer Links -->
                <div class="footer-links">

                    <!-- Company -->
                    <div class="footer-column">
                        <h4>Company</h4>

                        <a href="#">About</a>
                        <a href="#">Careers</a>
                        <a href="#">Press</a>
                    </div>

                    <!-- Support -->
                    <div class="footer-column">
                        <h4>Support</h4>

                        <a href="#">Account</a>
                        <a href="#">Help Center</a>
                        <a href="#">Contact Us</a>
                    </div>

                    <!-- Legal -->
                    <div class="footer-column">
                        <h4>Legals</h4>

                        <a href="#">Terms & Conditions</a>
                        <a href="#">Privacy Policy</a>
                        <a href="#">Licensing</a>
                    </div>

                </div>

            </div>

        </footer>
    `;
}

// Render footer automatically
renderFooter();