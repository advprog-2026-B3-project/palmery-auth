import { useState } from 'react'

export default function Login() {
  const [form, setForm] = useState({ email: '', password: '' })
  const [status, setStatus] = useState(null)

  const handleChange = (e) => setForm({ ...form, [e.target.name]: e.target.value })

  const handleSubmit = async (e) => {
    e.preventDefault()
    setStatus('loading')
    try {
      const res = await fetch('http://localhost:8080/api/login', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(form)
      })
      const data = await res.json()
      if (res.ok) setStatus({ ok: true, message: 'Logged in (mock)' })
      else setStatus({ ok: false, message: data.message || JSON.stringify(data) })
    } catch (err) {
      setStatus({ ok: false, message: err.message })
    }
  }

  return (
    <div style={{maxWidth:480, margin:'40px auto', fontFamily:'Arial, sans-serif'}}>
      <h2>Login</h2>
      <form onSubmit={handleSubmit}>
        <label>
          Email
          <input name="email" type="email" value={form.email} onChange={handleChange} required />
        </label>
        <br />
        <label>
          Password
          <input name="password" type="password" value={form.password} onChange={handleChange} required />
        </label>
        <br />
        <button type="submit">Login</button>
      </form>

      {status && (
        <div style={{marginTop:12}}>
          {status === 'loading' ? 'Sending...' : (
            <div style={{color: status.ok ? 'green' : 'red'}}>{status.message}</div>
          )}
        </div>
      )}
    </div>
  )
}
