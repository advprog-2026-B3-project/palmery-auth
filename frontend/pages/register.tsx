import { useState } from 'react'
import type { NextPage } from 'next'

type Form = { name: string; email: string; password: string; role: string }

const Register: NextPage = () => {
  const [form, setForm] = useState<Form>({ name: '', email: '', password: '', role: 'user' })
  const [status, setStatus] = useState<any>(null)

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => setForm({ ...form, [e.target.name]: e.target.value } as any)

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    setStatus('loading')
    try {
      const res = await fetch('http://localhost:8080/api/register', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(form)
      })
      const data = await res.json()
      if (res.ok) setStatus({ ok: true, message: data.message || 'Registered' })
      else setStatus({ ok: false, message: data.message || JSON.stringify(data) })
    } catch (err: any) {
      setStatus({ ok: false, message: err.message })
    }
  }

  return (
    <div className="page">
      <div className="card">
        <h2>Register</h2>
        <form onSubmit={handleSubmit}>
          <label>
            Name
            <input name="name" value={form.name} onChange={handleChange} required />
          </label>
          <label>
            Email
            <input name="email" type="email" value={form.email} onChange={handleChange} required />
          </label>
          <label>
            Password
            <input name="password" type="password" value={form.password} onChange={handleChange} required />
          </label>
          <label>
            Role
            <select name="role" value={form.role} onChange={handleChange}>
              <option value="user">User</option>
              <option value="admin">Admin</option>
              <option value="guest">Guest</option>
            </select>
          </label>
          <button type="submit">Register</button>
        </form>

        {status && (
          <div style={{marginTop:12}}>
            {status === 'loading' ? 'Sending...' : (
              <div style={{color: status.ok ? 'green' : 'red'}}>{status.message}</div>
            )}
          </div>
        )}
      </div>
    </div>
  )
}

export default Register
