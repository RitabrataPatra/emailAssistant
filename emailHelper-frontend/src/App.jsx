import { useState } from 'react'
import axios from 'axios';

import './App.css'
import CssBaseline from '@mui/material/CssBaseline';
import Box from '@mui/material/Box';
import Container from '@mui/material/Container';
import TextField from '@mui/material/TextField';
import Typography from '@mui/material/Typography';
import Select from '@mui/material/Select';
import InputLabel from '@mui/material/InputLabel';
import MenuItem from '@mui/material/MenuItem';
import FormControl from '@mui/material/FormControl';
import Button from '@mui/material/Button';
import { Bars, Circles, Grid, Hearts, Puff, Rings, TailSpin, ThreeDots } from 'react-loading-icons';


function App() {
  const [emailContent, setEmailContent] = useState("");
  const[ tone, setTone] = useState("");
  const[emailResponse ,setEmailResponse] = useState("");
  const [loading , setLoading] = useState(false);

  const generateEmailResponse = async () =>{
    try {
      const response  = await axios.post("http://localhost:8080/api/emails/generate", {
        emailContent: emailContent,
        tone: tone
      })
      // console.log(response.data);
      return response.data;
    } catch (error) {
      console.error("Error generating email response" , error);
    }
  }

  const handleSubmit = async (e) => {

    e.preventDefault();
    setLoading(true);
    try {
      const emailResponse = await generateEmailResponse();
      // console.log(emailResponse);
      setEmailResponse(emailResponse);
    } catch (error) {
      console.error("Error submitting the form" , error);
    }
    finally {
      setLoading(false);
    }
  }

  return (
    <>
      <CssBaseline />
      <Container maxWidth="md" sx={{ py: 4 }}>
        <Typography variant='h3' component={"h1"} gutterBottom>
          Email Reply Generator
        </Typography>
        <Box >
          <TextField
            label="Type your email here"
            multiline
            rows={2}
            fullWidth
            required
            variant="outlined"
            value={emailContent}
            onChange={(e) => setEmailContent(e.target.value)}
            sx={{mb:2}}
          />


          <FormControl fullWidth>
            <InputLabel>Tone (Optional)</InputLabel>
            <Select
              value={tone || ""}
              label="Tone (Optional)"
              onChange={(e) => setTone(e.target.value)}
              variant="outlined"

            >
              <MenuItem value="">None</MenuItem>
              <MenuItem value="Professional">Professional</MenuItem>
              <MenuItem value="Casual">Casual</MenuItem>
              <MenuItem value="Friendly">Friendly</MenuItem>
              <MenuItem value="Harsh">Harsh</MenuItem>
            </Select>
          </FormControl>
        </Box>
        <Box sx={{my : 4}}>
          <TextField 
          fullWidth
          multiline 
          rows={6}
          label="AI Generated Response" 
          variant="outlined"
           value={emailResponse} />
         
        </Box>
      
      <Box sx={{ display: 'flex', gap: 2 }}>
        <Button variant="contained"  {...(loading && { loading: true })}onClick={handleSubmit}>Generate</Button>

      <Button variant='outlined' onClick={()=> navigator.clipboard.write(emailResponse)}>Copy to Clipboard</Button>
      </Box>
      
      </Container>

    </>
  )
}

export default App
