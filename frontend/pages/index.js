import Link from 'next/link'

export default function Home() {
  return (
    <div style={{maxWidth:600, margin:'40px auto', fontFamily:'Arial, sans-serif'}}>
      <h1>Auth Frontend</h1>
      <p>Simple demo: Login & Register (multi-role)</p>
      <ul>
        <li><Link href="/login">Login</Link></li>
        <li><Link href="/register">Register</Link></li>
      </ul>
    </div>
  )
}
