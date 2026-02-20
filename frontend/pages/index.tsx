import Link from 'next/link'
import type { NextPage } from 'next'

const Home: NextPage = () => {
  return (
    <div className="page">
      <div className="card">
        <h1>Auth Frontend</h1>
        <p>Simple demo: Login & Register (multi-role)</p>
        <ul>
          <li><Link href="/login">Login</Link></li>
          <li><Link href="/register">Register</Link></li>
        </ul>
      </div>
    </div>
  )
}

export default Home
