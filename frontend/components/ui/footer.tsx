import "./footer.css";
export default function Footer() {
  return (
    <footer className="footer">
      <div className="footer-left">
        <img src="/palmery.svg" alt="Palmery logo" className="footer-logo" />
        <div>
          <h2>Palmery</h2>
          <p className="footer-tagline">Smart palm farm access</p>
        </div>
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