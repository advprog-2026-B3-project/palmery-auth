import { useState } from 'react'
import type { NextPage } from 'next'

type Form = { email: string; password: string }

const Login: NextPage = () => {
  const [form, setForm] = useState<Form>({ email: '', password: '' })
  const [status, setStatus] = useState<any>(null)

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => setForm({ ...form, [e.target.name]: e.target.value } as any)

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    setStatus('loading')
    try {
      const res = await fetch('http://localhost:8080/api/login', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(form)
      })
      const data = await res.json()
      if (res.ok) setStatus({ ok: true, message: 'Logged in (mock)', data })
      else setStatus({ ok: false, message: data.message || JSON.stringify(data) })
    } catch (err: any) {
      setStatus({ ok: false, message: err.message })
    }
  }

  return (
    <div className="page">
      <div className="card">
        <h2>Login</h2>
        <form onSubmit={handleSubmit}>
          <label>
            Email
            <input name="email" type="email" value={form.email} onChange={handleChange} required />
          </label>
          <label>
            Password
            <input name="password" type="password" value={form.password} onChange={handleChange} required />
          </label>
          <button type="submit">Login</button>
        </form>

        {status && (
          <div style={{marginTop:12}}>
            {status === 'loading' ? 'Sending...' : (
              <div style={{color: status.ok ? 'green' : 'red'}}>
                {status.ok ? `${status.message} - role: ${status.data?.role}` : status.message}
              </div>
            )}
          </div>
        )}
      </div>
    </div>
  )
}

export default Login
