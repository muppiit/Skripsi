/* eslint-disable react/prop-types */
/* eslint-disable no-unused-vars */
import React, { useState, useEffect } from "react";
import { Form, Input, Modal, DatePicker, Select, message } from "antd";
import { getKelas } from "@/api/kelas";
import { getReligions } from "@/api/religion";
import { getStudyPrograms } from "@/api/studyProgram";

const { TextArea } = Input;
const { Option } = Select;

const AddStudentForm = ({
  visible,
  onCancel,
  onOk,
  confirmLoading,
}) => {
  const [form] = Form.useForm();
  const [kelasList, setKelasList] = useState([]);
  const [filteredKelasList, setFilteredKelasList] = useState([]);
  const [studyProgramList, setStudyProgramList] = useState([]);
  const [religionList, setReligionList] = useState([]);

  const fetchData = async () => {
    try {
      const [kelasRes, religionRes, programRes] = await Promise.all([
        getKelas(),
        getReligions(),
        getStudyPrograms(),
      ]);
      if (kelasRes.data.statusCode === 200) setKelasList(kelasRes.data.content || []);
      if (religionRes.data.statusCode === 200) setReligionList(religionRes.data.content || []);
      if (programRes.data.statusCode === 200) setStudyProgramList(programRes.data.content || []);
    } catch (error) {
      message.error("Gagal memuat data: " + error.message);
    }
  };

  useEffect(() => {
    if (visible) {
      fetchData();
      form.resetFields();
    }
  }, [visible, form]);

  const handleStudyProgramChange = (studyProgramId) => {
    // Filter kelas berdasarkan study program yang dipilih
    const nextKelas = (kelasList || []).filter(
      (kelas) => kelas?.studyProgram?.id === studyProgramId || kelas?.study_program?.id === studyProgramId
    );
    setFilteredKelasList(nextKelas);
    form.setFieldsValue({ idKelas: undefined, idTahunAjaran: undefined, tahunAjaranDisplay: undefined });
  };

  const handleKelasChange = (kelasId) => {
    // Auto-populate Tahun Ajaran dari data Kelas yang dipilih
    const selectedKelas = (filteredKelasList || []).find(
      (kelas) => kelas.idKelas === kelasId
    );
    if (selectedKelas?.tahunAjaran) {
      form.setFieldsValue({
        idTahunAjaran: selectedKelas.tahunAjaran.idTahun,
        tahunAjaranDisplay: selectedKelas.tahunAjaran.tahunAjaran,
      });
    }
  };

  const handleSubmit = () => {
    form
      .validateFields()
      .then((values) => {
        onOk(values);
      })
      .catch((info) => {
        console.log("Validate Failed:", info);
      });
  };

  const formItemLayout = {
    labelCol: { xs: { span: 24 }, sm: { span: 8 } },
    wrapperCol: { xs: { span: 24 }, sm: { span: 16 } },
  };

  return (
    <Modal
      title="Tambah Mahasiswa"
      open={visible}
      onCancel={() => { form.resetFields(); onCancel(); }}
      onOk={handleSubmit}
      confirmLoading={confirmLoading}
      okText="Simpan"
      cancelText="Batal"
    >
      <Form form={form} {...formItemLayout}>
        <Form.Item
          label="ID Mahasiswa:"
          name="id"
          rules={[{ required: true, message: "Silahkan isikan ID mahasiswa" }]}
        >
          <Input placeholder="ID Mahasiswa" />
        </Form.Item>

        <Form.Item
          label="NISN:"
          name="nisn"
          rules={[{ required: true, message: "NISN wajib diisi" }]}
        >
          <Input placeholder="NISN" />
        </Form.Item>

        <Form.Item
          label="Nama Lengkap:"
          name="name"
          rules={[{ required: true, message: "Nama lengkap mahasiswa wajib diisi" }]}
        >
          <Input placeholder="Nama Lengkap Mahasiswa" />
        </Form.Item>

        <Form.Item
          label="Tempat Lahir:"
          name="place_born"
          rules={[{ required: true, message: "Tempat Lahir wajib diisi" }]}
        >
          <Input placeholder="Tempat Lahir" />
        </Form.Item>

        <Form.Item
          label="Tanggal Lahir:"
          name="birth_date"
          rules={[{ required: true, message: "Tanggal Lahir wajib diisi" }]}
        >
          <DatePicker placeholder="Tanggal Lahir" style={{ width: "100%" }} />
        </Form.Item>

        <Form.Item
          label="Gender:"
          name="gender"
          rules={[{ required: true, message: "Gender wajib diisi" }]}
        >
          <Select placeholder="Gender">
            <Option value="L">Laki-laki</Option>
            <Option value="P">Perempuan</Option>
          </Select>
        </Form.Item>

        <Form.Item
          label="Nomor Telepon:"
          name="phone"
          rules={[{ required: true, message: "Nomor telefon wajib diisi" }]}
        >
          <Input type="number" placeholder="Nomor Telefon (62)" />
        </Form.Item>

        <Form.Item
          label="Alamat:"
          name="address"
          rules={[{ required: true, message: "Alamat wajib diisi" }]}
        >
          <TextArea placeholder="Alamat" />
        </Form.Item>

        <Form.Item
          label="Agama:"
          name="religion_id"
          rules={[{ required: true, message: "Silahkan pilih agama" }]}
        >
          <Select placeholder="Pilih Agama">
            {religionList.map((arr) => (
              <Option value={arr.id} key={arr.id}>
                {arr.name}
              </Option>
            ))}
          </Select>
        </Form.Item>

        <Form.Item
          label="Program Studi:"
          name="idStudyProgram"
          rules={[{ required: true, message: "Silahkan pilih program studi" }]}
        >
          <Select
            placeholder="Pilih Program Studi"
            onChange={handleStudyProgramChange}
            showSearch
            optionFilterProp="children"
          >
            {studyProgramList.map((program) => (
              <Option key={program.id} value={program.id}>
                {program.name}
              </Option>
            ))}
          </Select>
        </Form.Item>

        <Form.Item
          label="Kelas:"
          name="idKelas"
          rules={[{ required: true, message: "Silahkan pilih kelas" }]}
        >
          <Select
            placeholder="Pilih Kelas"
            onChange={handleKelasChange}
            showSearch
            optionFilterProp="children"
          >
            {filteredKelasList.map((kelas) => (
              <Option key={kelas.idKelas} value={kelas.idKelas}>
                {kelas.namaKelas}
              </Option>
            ))}
          </Select>
        </Form.Item>

        {/* Hidden field untuk menyimpan idTahunAjaran */}
        <Form.Item name="idTahunAjaran" hidden>
          <Input />
        </Form.Item>

        <Form.Item label="Tahun Ajaran:" name="tahunAjaranDisplay">
          <Input placeholder="Otomatis terisi dari Kelas" disabled />
        </Form.Item>
      </Form>
    </Modal>
  );
};

export default AddStudentForm;
