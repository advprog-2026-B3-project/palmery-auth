import "./footer.css";
export default function Footer() {
  return (
    <footer className="footer">
      <div className="footer-left">
        <img src="/FYNE-11-1.png" alt="Palmery logo" className="footer-logo" />
        <h2>Palmery</h2>
      </div>

      <div className="footer-divider"></div>

      <div className="footer-center">
        <span>Home</span>
      </div>

      <div className="footer-right">
        <p>Contact Us:</p>
        <p>palmery@gmail.com</p>
      </div>
    </footer>
  );
}